package com.cetera.domain;

import com.cetera.enums.Status;
import com.cetera.enums.SystemUser;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Resources and Questionnaire Mapping table
 * Created by danni on 4/5/16.
 */
@Entity
public class ResourcesQa extends BaseDomain {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="SEQUENCE1")
    @SequenceGenerator(name="SEQUENCE1", sequenceName="RESOURCES_QA_SEQ", allocationSize=1)
    private Long id;
    private Long bdQaId;
    private Integer questionId;
    private Long resId;
    private Integer ranking;
    private Integer lowerLimit;
    private Integer upperLimit;
    private String version;
    private String status;

    public ResourcesQa() {}

    public ResourcesQa(Long bdQaId, Integer questionId, Long resId, Integer ranking,
                       Integer lowerLimit, Integer upperLimit, String version) {
        this.bdQaId = bdQaId;
        this.questionId = questionId;
        this.resId = resId;
        this.ranking = ranking;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.version = version;
        this.status = Status.ACTIVE.name();
        this.setCreatedBy(SystemUser.CETERA.name());
        this.setCreatedOn(new Date());
    }

    public ResourcesQa(ResourcesQa rq) {
        this.questionId = rq.getQuestionId();
        this.resId = rq.getResId();
        this.ranking = rq.getRanking();
        this.lowerLimit = rq.getLowerLimit();
        this.upperLimit = rq.getUpperLimit();
        this.version = new SimpleDateFormat("YYYYMMdd").format(new Date());
        this.status = Status.ACTIVE.name();
        this.setCreatedBy(SystemUser.ADMIN.name());
        this.setCreatedOn(new Date());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Long getResId() {
        return resId;
    }

    public void setResId(Long resId) {
        this.resId = resId;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(Integer lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public Integer getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(Integer upperLimit) {
        this.upperLimit = upperLimit;
    }

    public Long getBdQaId() {
        return bdQaId;
    }

    public void setBdQaId(Long bdQaId) {
        this.bdQaId = bdQaId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return  "bdQaId: " + bdQaId
        + "; questionId: " + questionId
        + "; resId: " + resId
        + "; ranking: " + ranking
        + "; lowerLimit: " + lowerLimit
        + "; upperLimit: " + upperLimit
        + "; version: " + version
        + "; status: " + status;
    }
}
