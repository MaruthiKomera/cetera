package com.cetera.domain;

import com.cetera.enums.YesOrNo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.Date;
import java.util.UUID;

/**
 * User Session table
 * @author sahilshah
 *
 */
@Entity
public class Sessions extends BaseDomain {
    @Id
    private String id;
    private String personId;
    private Date expiresOn;
    private String surveyStatus;
    
    @Enumerated(EnumType.STRING)
    @Column(length=1)
    private YesOrNo isInternal;

    public Sessions() {}

    public Sessions(Person person) {
        this.id = UUID.randomUUID().toString();
        this.personId = person.getId();
        this.setCreatedOn(new Date());
        this.setCreatedBy(person.getId());
        isInternal = YesOrNo.valueOf(person.getIsInternal());
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getPersonId() {
        return personId;
    }
    public void setPersonId(String personId) {
        this.personId = personId;
    }
    public Date getExpiresOn() {
        return expiresOn;
    }
    public void setExpiresOn(Date expiresOn) {
        this.expiresOn = expiresOn;
    }
    public String getSurveyStatus() {
        return surveyStatus;
    }
    public void setSurveyStatus(String surveyStatus) {
        this.surveyStatus = surveyStatus;
    }
    public YesOrNo getIsInternal() {
        return isInternal;
    }
    
    public void setIsInternal(YesOrNo isInternal) {
        this.isInternal = isInternal;
    }
}
