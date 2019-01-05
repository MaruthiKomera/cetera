package com.cetera.domain;

import com.cetera.model.AnswersRequest;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by danni on 4/6/16.
 * I_Quantify table keeps record of iQuantify value and performance.
 * Every time a user submits survey, a new entry will be created.
 */
@Entity
public class IQuantify extends BaseDomain {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="SEQUENCE1")
    @SequenceGenerator(name="SEQUENCE1", sequenceName="IQUANTIFY_SEQ", allocationSize=1)
    private Long id;
    private String personId;
    private Long questionnaireId;
    private BigDecimal performance;
    private Integer iQuantify; //0 ~ 99

    public IQuantify() {}

    public IQuantify(AnswersRequest answersRequest) {
        this.personId = answersRequest.getPersonId();
        this.questionnaireId = answersRequest.getQuestionnaireId();
        this.setCreatedBy(answersRequest.getPersonId());
        this.setCreatedOn(new Date());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public Long getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(Long questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public BigDecimal getPerformance() {
        return performance;
    }

    public void setPerformance(BigDecimal performance) {
        this.performance = performance;
    }

    public Integer getiQuantify() {
        return iQuantify;
    }

    public void setiQuantify(Integer iQuantify) {
        this.iQuantify = iQuantify;
    }
}
