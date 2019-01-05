package com.cetera.controllers.api;

import com.cetera.domain.Sessions;
import com.cetera.services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sahilshah
 *
 */
@RestController
@RequestMapping("/api/sessions")
public class SessionController {
    
    @Autowired
    SessionService sessionService;
    
    /**
     * Forces the current session to expire.
     * 
     * @param auth
     * @param sessionId
     */
    @RequestMapping(value = "/logout", method = RequestMethod.PATCH)
    public void logout(@RequestHeader("X-CS-Auth") String auth,
                       @RequestHeader("X-CS-Session") String sessionId) {
        sessionService.logout();
    }
    
    /**
     * Retrieves the current session.
     * 
     * @param auth
     * @param sessionId
     * @return Sessions
     */
    @RequestMapping(method = RequestMethod.GET)
    public Sessions retrieve(@RequestHeader("X-CS-Auth") String auth,
                             @RequestHeader("X-CS-Session") String sessionId) {
        return sessionService.retrieve();
    }

    /**
     * Renews the current session.
     *
     * @param auth
     * @param sessionId
     */
    @RequestMapping(value = "/renew", method = RequestMethod.PATCH)
    public void renew(@RequestHeader("X-CS-Auth") String auth,
                      @RequestHeader("X-CS-Session") String sessionId) {
        sessionService.renew();
    }
}
