package com.cetera.domain;

import java.util.List;

/**
 * Created by danni on 3/23/16.
 * Resources category mapping object
 */
public class CategoryDetails {
    private String category;
    private List<QuestionDetails> questionDetails;

    public CategoryDetails() {}

    public CategoryDetails(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<QuestionDetails> getQuestionDetails() {
        return questionDetails;
    }

    public void setQuestionDetails(List<QuestionDetails> questionDetails) {
        this.questionDetails = questionDetails;
    }
}
