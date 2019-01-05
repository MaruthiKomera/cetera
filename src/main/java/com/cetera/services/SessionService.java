package com.cetera.services;

import com.cetera.domain.Person;
import com.cetera.domain.Sessions;

/**
 * This service is to manage user session
 * @author sahilshah
 */
public interface SessionService {
    Sessions validate(String sessionId);
    void validateByPerson(String personId);
    void renew();
    void logout();
    Sessions save(Sessions session);
    Sessions create(Person person);
    Sessions retrieve();
    void updateSurveyStatus();
}
