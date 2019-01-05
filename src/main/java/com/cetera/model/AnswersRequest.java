package com.cetera.model;

import java.util.List;

/**
 * Answers Request API model
 * Created by danni on 3/28/16.
 */
public class AnswersRequest {
    private String personId;
    private Long questionnaireId;
    private List answer;
    public AnswersRequest() {}

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

    public List getAnswer() {
        return answer;
    }

    public void setAnswer(List answer) {
        this.answer = answer;
    }
}
