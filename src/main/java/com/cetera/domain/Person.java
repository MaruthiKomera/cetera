package com.cetera.domain;

import com.cetera.enums.YesOrNo;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * Person table
 */
@Entity
public class Person extends BaseDomain {
    // GUID for internal advisor; email for external advisor
    @Id
    private String id;
    private String firstName, lastName;
    private String email;
    private String consultantEmail;
    private Long bdId;
    private String isInternal;

    public Person() {}

    public Person(Payload payload) {
        this.id = payload.getUuid();
        this.firstName = payload.getFirstName();
        this.lastName = payload.getLastName();
        this.email = payload.getEmail();
        this.consultantEmail = payload.getBusConsEmail();
        this.isInternal = YesOrNo.Y.name();
        this.setCreatedBy(this.id);
        this.setCreatedOn(new Date());
    }

    public void update(Person person) {
        if (!this.firstName.equals(person.getFirstName())
            || !this.lastName.equals(person.getLastName())
            || !this.email.equals(person.getEmail())) {
            this.firstName = person.getFirstName();
            this.lastName = person.getLastName();
            this.email = person.getEmail();
            this.setUpdatedBy(this.id);
            this.setUpdatedOn(new Date());
        }
    }

    public String getId() {
        return id;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return "Person [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + "]";
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getConsultantEmail() {
        return consultantEmail;
    }

    public void setConsultantEmail(String consultantEmail) {
        this.consultantEmail = consultantEmail;
    }



    public String getIsInternal() {
        return isInternal;
    }

    public void setIsInternal(String isInternal) {
        this.isInternal = isInternal;
    }


    public Long getBdId() {
        return bdId;
    }

    public void setBdId(Long bdId) {
        this.bdId = bdId;
    }
}
