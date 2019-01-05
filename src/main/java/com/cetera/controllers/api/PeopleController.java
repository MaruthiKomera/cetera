package com.cetera.controllers.api;

import com.cetera.domain.Person;
import com.cetera.domain.Sessions;
import com.cetera.model.PersonRegisterRequest;
import com.cetera.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/people")
public class PeopleController {

    @Autowired
    private PersonService personService;

    /**
     * External Advisor Uses the person details provided to update or create
     * person entry in the database. Also updates or creates revenue and session
     * entries in the database linked to the person created.
     * 
     * @param auth
     * @param request
     * @return Sessions
     */
    @RequestMapping(value = "/external", method = RequestMethod.PUT)
    public Sessions createExternalAdvisor(@RequestHeader("X-CS-Auth") String auth,
                                          @RequestBody PersonRegisterRequest request) {
        return personService.create(request);
    }

    /**
     * Internal Advisor Receives the encrypted payload, decrypts it and joins
     * into the flow for registering the person.
     * 
     * @param auth
     * @param encryptedPayload
     * @return Sessions
     */
    @RequestMapping(value = "/internal", method = RequestMethod.PUT)
    public Sessions createInternalAdvisor(@RequestHeader("X-CS-Auth") String auth,
                                      @RequestBody String encryptedPayload) {
        return personService.create(encryptedPayload);
    }

    /**
     * get person object by session
     * @param auth
     * @param sessionId
     * @return Person
     */
    @RequestMapping(method = RequestMethod.GET)
    public Person get(@RequestHeader("X-CS-Auth") String auth,
                    @RequestHeader("X-CS-Session") String sessionId) {
        return personService.get();
    }

}
