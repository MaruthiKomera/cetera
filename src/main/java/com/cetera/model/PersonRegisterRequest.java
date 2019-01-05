package com.cetera.model;

import com.cetera.domain.Person;
import com.cetera.domain.Revenue;

/**
 * External advisor Request API model
 */
public class PersonRegisterRequest {
    private Person person;
    private Revenue revenue;

    public PersonRegisterRequest() {}

    public PersonRegisterRequest(Person person, Revenue revenue) {
        this.person = person;
        this.revenue = revenue;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Revenue getRevenue() {
        return revenue;
    }

    public void setRevenue(Revenue revenue) {
        this.revenue = revenue;
    }

}
