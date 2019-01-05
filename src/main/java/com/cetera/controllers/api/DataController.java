package com.cetera.controllers.api;

import com.cetera.domain.Questionnaire;
import com.cetera.domain.Resources;
import com.cetera.services.QuestionnaireService;
import com.cetera.services.ResourcesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * CRUD resource links and questionnaires
 * This controller is for Admin
 * Created by danni on 5/20/16.
 */
@RestController
@RequestMapping("/api/data")
public class DataController {
    private static Logger logger = LoggerFactory.getLogger(DataController.class);

    @Autowired
    private ResourcesService resourcesService;

    @Autowired
    private QuestionnaireService questionnaireService;

    /**
     * create
     * only provide create questionnaire function
     * @param auth
     * @param passkey
     */
    //create questionnaire record, must edit mapping table, and mark previous records as INACTIVE
    @RequestMapping(value = "/questionnaire/{bdId}/{expand}", method = RequestMethod.POST)
    public Questionnaire createQuestionnaire(@RequestHeader("X-CS-Auth") String auth,
                                             @RequestHeader("X-CS-Passkey") String passkey,
                                             @PathVariable("bdId") Integer bdId,
                                             @PathVariable("expand") Boolean expand,
                                             @RequestParam("file") MultipartFile questionFile) {
        return questionnaireService.add(bdId, questionFile, expand);
    }

    /**
     * read
     * @param auth
     * @param passkey
     */
    //get all resources
    @RequestMapping(value = "/resources/_active", method = RequestMethod.GET)
    public List<Resources> getAllResources(@RequestHeader("X-CS-Auth") String auth,
                                           @RequestHeader("X-CS-PassKey") String passkey) {
        return resourcesService.getAll();
    }

    //get all active questionnaires
    @RequestMapping(value = "/questionnaire/_active", method = RequestMethod.GET)
    public List<Questionnaire> getAllQuestionnaire(@RequestHeader("X-CS-Auth") String auth,
                                    @RequestHeader("X-CS-Passkey") String passkey) {
        return questionnaireService.getAll();
    }

    /**
     * update resources and questionnaire
     * update resources in place
     * update questionnaire by creating new questionnaire, and mark the previous on as INACTIVE
     * @param auth
     * @param passkey
     * @param resourceId
     */
    @RequestMapping(value = "/resources/{resourceId}", method = RequestMethod.POST)
    public Resources updateResource(@RequestHeader("X-CS-Auth") String auth,
                                    @RequestHeader("X-CS-Passkey") String passkey,
                                    @PathVariable("resourceId") Long resourceId,
                                    @RequestParam("file") MultipartFile resourceFile) {
        return resourcesService.update(resourceId, resourceFile);
    }

//    @RequestMapping(value = "/questionnaire/{questionnaireId}", method = RequestMethod.POST)
//    public Questionnaire updateQuestionnaire(@RequestHeader("X-CS-Auth") String auth,
//                                             @RequestHeader("X-CS-Passkey") String passkey,
//                                             @PathVariable("questionnaireId") Long questionnaireId,
//                                             @RequestParam("file") MultipartFile questionFile) {
//        logger.debug("newQuestionnaire is {}", questionFile);
//        return questionnaireService.update(questionnaireId, questionFile);
//    }

    /**
     * delete resources
     * comment out this api since we are not using it right now
     * @param auth
     * @param sessionId
     * @param resourceId
     */
    //cannot delete(mark as INACTIVE) questionnaire directly,
    // must create new one and then delete old one, or update mapping table directly
    // to remove/edit an active mapping for a questionnaire

    //can delete resources by changing resources_qa status column to INACTIVE

    //change RESOURCES_QA status to INACTIVE
//    @RequestMapping(value = "/resources/{resourceId}", method = RequestMethod.DELETE)
//    public void deleteResource(@RequestHeader("X-CS-Auth") String auth,
//                               @RequestHeader("X-CS-Session") String sessionId,
//                               @PathVariable("resourceId") Long resourceId) {
//        resourcesService.delete(resourceId);
//    }
}
