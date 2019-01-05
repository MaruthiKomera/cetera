package com.cetera.services;

import com.cetera.domain.Questionnaire;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * This service is used to create Questionnaire seed data and retrieve correct questionnaire for different users
 * Created by danni on 3/23/16.
 */
public interface QuestionnaireService {
    Questionnaire retrieve(String name);
    void create(String dataSeededAsOfDate);
    List<Questionnaire> getAll();

    //Admin functions to manage questionnaire data
    Questionnaire findOne(Long id);
    Questionnaire update(Long id, MultipartFile newQuestionnaire);
    Questionnaire add(Integer bdId, MultipartFile questionFile, Boolean expand);
}
