package com.cetera.domain;

import java.util.List;

/**
 * Mapping object for each question inside questionnaire
 * Created by danni on 3/23/16.
 */
public class QuestionDetails {
    private String description;
    private String answerType;
    private List<String> answerOptions;
    private List weight;

    public QuestionDetails() {}

    public QuestionDetails(String description, String answerType,
                           List<String> answerOptions, List weight) {
        this.description = description;
        this.answerType = answerType;
        this.answerOptions = answerOptions;
        this.weight = weight;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    public List getWeight() {
        return weight;
    }

    public void setWeight(List weight) {
        this.weight = weight;
    }

    public List<String> getAnswerOptions() {
        return answerOptions;
    }

    public void setAnswerOptions(List<String> answerOptions) {
        this.answerOptions = answerOptions;
    }
}
