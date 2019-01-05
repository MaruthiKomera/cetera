package com.cetera.services;

import java.util.Map;

/**
 * This service is used to create Resources and Questionnaire mapping seed data
 * Created by danni on 4/5/16.
 */
public interface ResourcesQaService {
    void create(String dataSeededAsOfDate);
    void update(Map<Long, Long> bdQaIdChanges);
}
