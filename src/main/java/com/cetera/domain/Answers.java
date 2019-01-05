package com.cetera.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Date;

/**
 * Created by danni on 3/23/16.
 * Answers domain keeps records of answers and weights for questionnaire
 * Every time a user submits a survey, a new entry is created
 */
@Entity
public class Answers extends BaseDomain {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="SEQUENCE1")
    @SequenceGenerator(name="SEQUENCE1", sequenceName="ANSWERS_SEQ", allocationSize=1)
    private Long id;
    private Long iQuantifyId;
    private Integer questionId;
    private String question;
    private String answer;
    private Integer weight;

    public Answers() {}

    public Answers(Integer questionId, String question, String answer, Integer weight) {
        this.questionId = questionId;
        this.question = question;
        this.answer = answer;
        this.weight = weight;
        this.setCreatedOn(new Date());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Long getiQuantifyId() {
        return iQuantifyId;
    }

    public void setiQuantifyId(Long iQuantifyId) {
        this.iQuantifyId = iQuantifyId;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
