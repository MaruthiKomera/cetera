package com.cetera.domain;

import com.cetera.enums.Status;
import com.cetera.enums.SystemUser;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;
import java.util.Date;

/**
 * Questionnaire table
 * Created by danni on 3/23/16.
 */
@Entity
public class Questionnaire extends BaseDomain {
    @Id
    private Long id;
    private String name;
    @Lob
    @Basic
    private String questions;
    private String version;
    private String status;

    @Transient
    private String bdId;

    @Transient
    private Integer bdIdForApi;

    @Transient
    private String bdIdGroup;


    public Questionnaire() {}

    public Questionnaire(String questions) {
        this.questions = questions;
    }

    public Questionnaire(Long id, String name, String questions) {
        this.id = id;
        this.name = name;
        this.questions = questions;
    }

    public Questionnaire(Long id, String name, String version, String questions) {
        this.id = id;
        this.version = version;
        this.questions = questions;
        this.status = Status.ACTIVE.name();
        this.name = name;
        this.setCreatedBy(SystemUser.CETERA.name());
        this.setCreatedOn(new Date());
    }

    public Questionnaire(Long id, String questions) {
        this.id = id;
        this.questions = questions;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBdId() {
        return bdId;
    }

    public void setBdId(String bdId) {
        this.bdId = bdId;
    }

    public String getBdIdGroup() {
        return bdIdGroup;
    }

    public void setBdIdGroup(String bdIdGroup) {
        this.bdIdGroup = bdIdGroup;
    }

    public Integer getBdIdForApi() {
        return bdIdForApi;
    }

    public void setBdIdForApi(Integer bdIdForApi) {
        this.bdIdForApi = bdIdForApi;
    }
}
