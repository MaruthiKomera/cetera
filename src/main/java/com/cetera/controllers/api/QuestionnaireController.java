package com.cetera.controllers.api;

import com.cetera.domain.Questionnaire;
import com.cetera.services.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by danni on 3/23/16.
 */
@RestController
@RequestMapping("/api/questions")
public class QuestionnaireController {

    @Autowired
    private QuestionnaireService questionService;

    /**
     * get latest DOL QA
     * @param auth
     * @param sessionId
     * @param name
     * @return Questionnaire
     */
    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    public Questionnaire get(@RequestHeader("X-CS-Auth") String auth,
                             @RequestHeader(value = "X-CS-Session", required = false) String sessionId,
                             @PathVariable("name") String name) {
        return questionService.retrieve(name);
    }
}
