package com.cetera.services;

import com.cetera.dao.PersonRepository;
import com.cetera.domain.CurrentSession;
import com.cetera.domain.Payload;
import com.cetera.domain.Person;
import com.cetera.domain.Revenue;
import com.cetera.domain.Sessions;
import com.cetera.enums.YesOrNo;
import com.cetera.exceptions.PfmException;
import com.cetera.exceptions.PfmExceptionCode;
import com.cetera.model.PersonRegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Date;
import java.util.regex.Pattern;

@Service
public class PersonServiceImpl implements PersonService {

    private static Logger logger = LoggerFactory.getLogger(PersonServiceImpl.class);
    
    @Autowired
    private PersonRepository personRepository;
    
    @Autowired
    private RevenueService revenueService;
    
    @Autowired
    private SessionService sessionService;

    @Autowired
    private CurrentSession currentSession;
    
    @Autowired
    private EncryptedPayloadService payloadService;

    @Autowired
    private BrokerDealerService brokerDealerService;

    @Value("${broker.dealer.count}")
    private Integer bdCount;
    
    @Value("${payload.validity.seconds}")
    private Long payloadValidity;

    @Autowired
    private EncryptionService encryptionService;

    @Override
    public Person get() {
        return findOne(currentSession.getSession().getPersonId());
    }

    @Override
    public Person add(Person person) {
        personRepository.save(person);
        return personRepository.findOne(person.getId());
    }

    @Override
    public Person findOne(String id) {
        return personRepository.findOne(id);
    }

    @Override
    public Person get(String id) {
        return personRepository.findOne(id);
    }

    @Override
    public Person update(Person person) {
        if (personRepository.findOne(person.getId()) != null) {
            personRepository.save(person);
        }
        return personRepository.findOne(person.getId());
    }

    @Override
    public Sessions create(PersonRegisterRequest request) {
        Person person = request.getPerson();
        Revenue revenue = request.getRevenue();

        validatePersonRequest(person, revenue);

        person.setId(person.getEmail());
        Person savedPerson = personRepository.findOne(person.getId());
        if (savedPerson == null) {
            person.setCreatedOn(new Date());
            person.setCreatedBy(person.getId());
            person.setIsInternal(YesOrNo.N.name());
            person.setBdId(brokerDealerService.findByExternalId(null).getId());
            savedPerson = personRepository.save(person);
        } else {
            /*
             * Since the person is present, the saved person has to be updated.
             * Assumption: The difference would be only in the first name, last name or email.
             * Here email can't be different for an external advisor but can change for an
             * internal one.
             */
            savedPerson.update(person);
            savedPerson = personRepository.save(savedPerson);
        }

        /*
         * Now that the person is updated into the database, the revenue figures associated
         * with the person can be updated.
         */
        revenue.setPersonId(savedPerson.getId());
        revenueService.create(revenue);

        /*
         * Finally after all the data related to the person and revenue has been saved,
         * we can create a session for the person. This is a soft-session because the authentication
         * is not stringent as it would normally be.
         */
        return sessionService.create(savedPerson);
    }

    private void validatePersonRequest(Person person, Revenue revenue) {
        if (person == null) {
            throw new PfmException("Person object missing in request", PfmExceptionCode.PERSON_NULL);
        }
        /*
         * Check the mandatory fields - First Name, Last Name and Email.
         *
         * Assumption: These fields would definitely be provided for the
         * internal advisors too.
         */

        String nameRegex = "^[a-zA-Z.' ,-]{1,50}$";

        String firstName = person.getFirstName();
        if (!StringUtils.hasText(firstName)) {
            throw new PfmException("Person information lacking first name", PfmExceptionCode.PERSON_MISSING_FIRSTNAME);
        }
        firstName = firstName.trim();
        if (!Pattern.matches(nameRegex, firstName)) {
            throw new PfmException("Firstname should be between 1 and 50 characters. Valid Characters are a-z A-Z . ' -",
                PfmExceptionCode.PERSON_INVALID_FIRSTNAME);
        }
        person.setFirstName(firstName);

        String lastName = person.getLastName();
        if (!StringUtils.hasText(lastName)) {
            throw new PfmException("Person information lacking last name", PfmExceptionCode.PERSON_MISSING_LASTNAME);
        }
        lastName = lastName.trim();
        if (!Pattern.matches(nameRegex, lastName)) {
            throw new PfmException("Lastname should be between 1 and 50 characters. Valid Characters are a-z A-Z . ' -",
                PfmExceptionCode.PERSON_INVALID_LASTNAME);
        }
        person.setLastName(lastName);

        String email = person.getEmail();
        if (!StringUtils.hasText(email)) {
            throw new PfmException("Person information lacking email", PfmExceptionCode.PERSON_MISSING_EMAIL);
        }
        email = email.toLowerCase().trim();

        String emailRegex = "^([\\w\\.\\-\\+])+@(([a-z0-9\\-])+\\.)+([a-z0-9\\-])+$";
        if (!Pattern.matches(emailRegex, email)) {
            throw new PfmException("Invalid contact email.", PfmExceptionCode.PERSON_INVALID_EMAIL);
        }
        person.setEmail(email);

        revenueService.validateRevenue(revenue);
    }

	@Override
	public Sessions create(String encryptedPayload) {
	    
        /*
         * Saving the encrypted payload information into DB.
         */
        payloadService.create(encryptedPayload);
	    
	    /*
	     * Decrypting the payload and creating the personRegisterRequest which would
	     * be used for person creation.
	     */
		String payloadString = encryptionService.decrypt(encryptedPayload);
        /*
         * Extracting the payload information.
         */
        Payload payload;
        try {
            payload = new ObjectMapper().readValue(payloadString, Payload.class);
        } catch (IOException e) {
            logger.debug("Cannot construct payload object. {}", e.getMessage());
            throw new PfmException("Cannot construct payload object.", PfmExceptionCode.JSON_PROCESSING_EXCEPTION);
        }

        Person person = new Person(payload);
        Revenue revenue = new Revenue(payload);
        validatePayload(payload, person, revenue);

        Person savedPerson = personRepository.findOne(person.getId());
        if (savedPerson != null) {
            savedPerson.update(person);
            if (!brokerDealerService.findByExternalId(payload.getBd()).getId().equals(savedPerson.getBdId())
                || payload.getBusConsEmail() != null
                && !payload.getBusConsEmail().equals(savedPerson.getConsultantEmail())
                || payload.getBusConsEmail() == null && savedPerson.getConsultantEmail() != null) {
                savedPerson.setBdId(brokerDealerService.findByExternalId(payload.getBd()).getId());
                savedPerson.setConsultantEmail(payload.getBusConsEmail());
                savedPerson.setUpdatedOn(new Date());
                savedPerson.setUpdatedBy(savedPerson.getId());
            }
            personRepository.save(savedPerson);
            revenueService.create(revenue);
            return sessionService.create(savedPerson);
        } else {
            person.setBdId(brokerDealerService.findByExternalId(payload.getBd()).getId());
            personRepository.save(person);
            revenueService.create(revenue);
            return sessionService.create(person);
        }
	}

    private void validatePayload(Payload payload, Person person, Revenue revenue) {
        if (!StringUtils.hasText(payload.getUuid())) {
            throw new PfmException("Missing uuid in encrypted payload.", PfmExceptionCode.PAYLOAD_MISSING_UUID);
        }

        if (payload.getBd() == null) {
            throw new PfmException("Missing bd Id in encrypted payload.", PfmExceptionCode.PAYLOAD_MISSING_BD);
        }
        if (brokerDealerService.findByExternalId(payload.getBd()) == null) {
            throw new PfmException("Invalid bd Id in encrypted payload.", PfmExceptionCode.PAYLOAD_INVALID_BD);
        }
        if (payload.getTimestamp() == null) {
            throw new PfmException("Missing timestamp in encrypted payload.", PfmExceptionCode.PAYLOAD_MISSING_TIMESTAMP);
        }
        /*
         * Check whether the payload timestamp is within the accepted delay for
         * payload expiry.
         *
         * Reason: This is to prevent the request sender from saving an encrypted payload
         * and replaying the request.
         */
        if (payloadValidity < (new Date().getTime() - payload.getTimestamp().getTime())) {
            throw new PfmException("Validity of the encrypted payload has expired.", PfmExceptionCode.PAYLOAD_EXPIRED);
        }

        //disable validation for internal advisors
        //validatePersonRequest(person, revenue);
    }
}
