package com.cetera.services;

import com.cetera.model.AnswersRequest;
import com.cetera.model.AnswersResponse;

/**
 * This service handle new survey and retrieve survey result request
 * Created by danni on 3/23/16.
 */
public interface AnswersService {
    AnswersResponse create(AnswersRequest answersRequest);
    AnswersResponse retrieve();
    AnswersResponse retrieve(String personId);
}
