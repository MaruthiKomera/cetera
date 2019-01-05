package com.cetera.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Questionnaire mapping object
 * Created by danni on 3/23/16.
 */
public class QuestionnaireMapping {
    private Integer questionCount;
    private List<CategoryDetails> categoryDetails;

    public QuestionnaireMapping() {}

    public QuestionnaireMapping(Integer questionCount) {
        this.questionCount = questionCount;
        this.categoryDetails = new ArrayList<CategoryDetails>();
    }

    public List<CategoryDetails> getCategoryDetails() {
        return categoryDetails;
    }

    public void setCategoryDetails(List<CategoryDetails> categoryDetails) {
        this.categoryDetails = categoryDetails;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }
}
