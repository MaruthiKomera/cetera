package com.cetera.services;

import com.cetera.domain.Person;
import com.cetera.domain.Sessions;
import com.cetera.model.PersonRegisterRequest;

/**
 * This service is used to manage person data
 */
public interface PersonService {

    Person add(Person person);

    Person get(String id);

    Person update(Person person);

    Sessions create(PersonRegisterRequest request);
    
    Sessions create(String encryptedPayload);
        
    Person findOne(String id);
    Person get();
}
