package com.cetera.domain;

import com.cetera.enums.SystemUser;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by danni on 4/10/16.
 *  Multi-to-one mapping between broker_dealer and Questionnaire
 */
@Entity
public class BdQa extends BaseDomain {
    @Id
    private Long id;
    private Long bdId;
    private Long questionnaireId;
    private String version;
    private String status;

    public BdQa() {}

    public BdQa(Long id, Long bdId, Long questionnaireId, String version, String status) {
        this.id = id;
        this.bdId = bdId;
        this.questionnaireId = questionnaireId;
        this.version = version;
        this.status = status;
        this.setCreatedBy(SystemUser.CETERA.name());
        this.setCreatedOn(new Date());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBdId() {
        return bdId;
    }

    public void setBdId(Long bdId) {
        this.bdId = bdId;
    }

    public Long getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(Long questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
