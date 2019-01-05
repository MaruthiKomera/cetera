package com.cetera.controllers.api;

import com.cetera.model.SsoRequest;
import com.cetera.services.ResourcesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by danni on 4/11/16.
 */
@RestController
@RequestMapping("/api/resources")
public class ResourcesController {
    private static Logger logger = LoggerFactory.getLogger(ResourcesController.class);

    @Autowired
    private ResourcesService resourcesService;

    /**
     * get sso resource form values
     * @param auth
     * @param sessionId
     * @param target
     * @return SsoRequest
     */
    @RequestMapping(method = RequestMethod.GET)
    public SsoRequest getSsoRequest(@RequestHeader("X-CS-Auth") String auth,
                                    @RequestHeader("X-CS-Session") String sessionId,
                                    @RequestParam("target") String target) {
        return resourcesService.getSsoValue(target);
    }
}
