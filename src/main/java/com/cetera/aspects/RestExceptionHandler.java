package com.cetera.aspects;

import com.cetera.domain.BusinessError;
import com.cetera.exceptions.PfmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by danni on 3/22/16.
 */
@ControllerAdvice(annotations = RestController.class)
public class RestExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BusinessError> handleException(Exception ex) {
        logger.debug("in the ExceptionHandlerController..{}", ex.getClass().getName());
        return PfmException.handleException(ex);
    }
}
