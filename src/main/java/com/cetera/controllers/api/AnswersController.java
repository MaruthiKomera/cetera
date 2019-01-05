package com.cetera.controllers.api;

import com.cetera.model.AnswersRequest;
import com.cetera.model.AnswersResponse;
import com.cetera.services.AnswersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * answers controller manage all answers from user, either retrieve or put
 * Created by danni on 3/23/16.
 */
@RestController
@RequestMapping("/api/answers")
public class AnswersController {
    private static Logger logger = LoggerFactory.getLogger(AnswersController.class);


    @Autowired
    private AnswersService answersService;

    /**
     * create new answers for questionnaire
     * @param auth
     * @param sessionId
     * @param answersRequest
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    public AnswersResponse create(@RequestHeader
    		("X-CS-Auth") String auth,
                                  @RequestHeader("X-CS-Session") String sessionId,
                                  @RequestBody AnswersRequest answersRequest) {
        return answersService.create(answersRequest);
    }

    /**
     * get last AnswersResponse for the current session
     * @param auth
     * @param sessionId
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public AnswersResponse get(@RequestHeader("X-CS-Auth") String auth,
                             @RequestHeader("X-CS-Session") String sessionId) {
        return answersService.retrieve();
    }
}
