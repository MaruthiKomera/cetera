package com.cetera.model;

import com.cetera.domain.ResourceDetail;

import java.math.BigDecimal;
import java.util.List;

/**
 * Answers Response API model
 * Created by danni on 3/23/16.
 */
public class AnswersResponse {

    private Integer iQuantify;
    private BigDecimal performance; //better then % others
    private List<ResourceDetail> advisorResources;

    public AnswersResponse() {}

    public AnswersResponse(Integer iQuantify, BigDecimal performance) {
        this.iQuantify = iQuantify;
        this.performance = performance;
    }

    public Integer getiQuantify() {
        return iQuantify;
    }

    public void setiQuantify(Integer iQuantify) {
        this.iQuantify = iQuantify;
    }

    public BigDecimal getPerformance() {
        return performance;
    }

    public void setPerformance(BigDecimal performance) {
        this.performance = performance;
    }

    public List<ResourceDetail> getAdvisorResources() {
        return advisorResources;
    }

    public void setAdvisorResources(List<ResourceDetail> advisorResources) {
        this.advisorResources = advisorResources;
    }
}
