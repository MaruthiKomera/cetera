package com.cetera.services;

import com.cetera.dao.SessionRepository;
import com.cetera.domain.CurrentSession;
import com.cetera.domain.Person;
import com.cetera.domain.Sessions;
import com.cetera.enums.SurveyStatus;
import com.cetera.exceptions.PfmException;
import com.cetera.exceptions.PfmExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author sahilshah
 *
 */
@Service
public class SessionServiceImpl implements SessionService {
    private static Logger logger = LoggerFactory.getLogger(SessionServiceImpl.class);

    //2 hour valid time for session
    private static final long HOUR2 = 3600 * 1000 * 2;
    @Autowired
    private SessionRepository sessionRepository;
    
    @Autowired
    private CurrentSession currentSession;
    
    @Autowired
    private AnswersService answersService;
    /**
     * only person himself can edit his stuff
     * it only validate when currentSesison is not null
     * means only validate api request which require session info
     * @param personId
     */
    @Override
    public void validateByPerson(String personId) {
        Sessions session = currentSession.getSession();
        if (session != null &&
            (session.getPersonId() == null || !session.getPersonId().equals(personId))) {
            throw new PfmException("Permission denied.", PfmExceptionCode.SERVICE_PERMISSION_DENIED);
        }
    }

    /**
     * Validate session exists and not expired
     * @param sessionId
     * @return
     * @throws PfmException
     */
    @Override
    public Sessions validate(String sessionId) throws PfmException {
        Sessions session = sessionRepository.findOne(sessionId);
        if (session == null) {
            throw new PfmException("Session invalid", PfmExceptionCode.SESSION_INVALID);
        }

        Date currentTime = new Date();
        if (currentTime.after(session.getExpiresOn())) {
            throw new PfmException("Session expired.", PfmExceptionCode.SESSION_EXPIRED);
        }
        return session;
    }

    /**
     * renew current session with 2 hours
     */
    @Override
    public void renew() {
        Sessions session = currentSession.getSession();
        session.setExpiresOn(new Date(new Date().getTime() + HOUR2));
        session.setUpdatedBy(session.getPersonId());
        session.setUpdatedOn(new Date());
        sessionRepository.save(session);
    }

    /**
     * Expire current session
     */
    @Override
    public void logout() {
        Sessions session = currentSession.getSession();
        Date currentTime = new Date();
        if (currentTime.after(session.getExpiresOn())) {
            return;
        }
        session.setExpiresOn(new Date());
        session.setUpdatedBy(session.getPersonId());
        session.setUpdatedOn(new Date());
        save(session);
    }
    
    @Override
    public Sessions save(Sessions session) {
        return sessionRepository.save(session);
    }

    @Override
    public Sessions retrieve() {
        return currentSession.getSession();
    }
    
    @Override
    public Sessions create(Person person) {
        if (person == null) {
            throw new PfmException("Cannot create a session because the person object is null", PfmExceptionCode.PERSON_NULL);
        }

        Sessions session = new Sessions(person);
        session.setExpiresOn(new Date(new Date().getTime() + HOUR2));

        if (answersService.retrieve(person.getId()) == null) {
            session.setSurveyStatus(SurveyStatus.NOT_TAKEN.name());
        } else {
            session.setSurveyStatus(SurveyStatus.COMPLETED.name());
        }

        return sessionRepository.save(session);
    }

    @Override
    public void updateSurveyStatus() {
        Sessions session = currentSession.getSession();
        session.setSurveyStatus(SurveyStatus.COMPLETED.name());
        save(session);
    }
}
