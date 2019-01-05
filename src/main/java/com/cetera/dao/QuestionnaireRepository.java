package com.cetera.dao;

import com.cetera.domain.Questionnaire;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Repository for Questionnaire table
 * Created by danni on 3/23/16.
 */
public interface QuestionnaireRepository extends PagingAndSortingRepository<Questionnaire, Long> {
    List<Questionnaire> findByVersion(String version);
    List<Questionnaire> findByStatus(String status);
}
