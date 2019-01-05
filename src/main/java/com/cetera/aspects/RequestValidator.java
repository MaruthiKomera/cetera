package com.cetera.aspects;

import com.cetera.domain.CurrentSession;
import com.cetera.domain.PasswordHash;
import com.cetera.domain.Sessions;
import com.cetera.exceptions.PfmException;
import com.cetera.exceptions.PfmExceptionCode;
import com.cetera.services.SessionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by danni on 3/22/16.
 */
@Component
public class RequestValidator {
    private static Logger logger = LoggerFactory.getLogger(RequestValidator.class);

    @Value("${cetera.apiKey}")
    private String ceteraApiKey;

    @Value("${cetera.dol.admin.passkey}")
    private String adminPasskey;
    
    @Autowired
    private CurrentSession currentSession;

    @Autowired
    private SessionService sessionService;

    /**
     *
     * @param uri
     * @param apiKey
     * @param sessionId
     * @return
     */
    public void validate(String uri, String apiKey, String sessionId, String passkey) {
        if (uri.startsWith("/api")) {

            /**
             * api key is always required
             */
            if (apiKey == null || !ceteraApiKey.equals(apiKey)) {
                logger.debug("exception invalid api key");
                throw new PfmException("ApiKey is invalid!", PfmExceptionCode.SERVICE_API_KEY_INVALID);
            }

            if (sessionId != null) {
                Sessions session = sessionService.validate(sessionId);
                currentSession.setSession(session);
                sessionService.renew();
            }

            /**
             * for admin function to manage resource/questionnaire data
             * need to validate Admin session
             */
            if (uri.startsWith("/api/data")) {
                boolean validKey;
                try {
                    validKey = PasswordHash.validatePassword(passkey, adminPasskey);
                } catch(NoSuchAlgorithmException | InvalidKeySpecException e) {
                    logger.debug("Cannot validate passkey. {}", e.getMessage());
                    throw new PfmException("Cannot validate passkey." + e.getMessage());
                }
                if (!validKey) {
                    throw new PfmException("Invalid passkey.", PfmExceptionCode.SERVICE_PERMISSION_DENIED);
                }

            }
        }
    }
}

