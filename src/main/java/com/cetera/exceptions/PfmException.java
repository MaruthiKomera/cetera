package com.cetera.exceptions;

import com.cetera.domain.BusinessError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Handle system exception
 * Created by danni on 3/22/16.
 */
public class PfmException extends RuntimeException {
    private static Logger logger = LoggerFactory.getLogger(PfmException.class);

    private PfmExceptionCode code;

    public PfmException(String message) {
        super(message);
        setCode(PfmExceptionCode.GENERIC);
    }

    public PfmException(String message, PfmExceptionCode code) {
        super(message);
        this.setCode(code);
    }

    public PfmException(String message, Exception e) {
        super(message, e);
        this.setStackTrace(e.getStackTrace());
        setCode(PfmExceptionCode.GENERIC);
    }

    public PfmException(String message, PfmExceptionCode code, Exception e) {
        super(message, e);
        this.setStackTrace(e.getStackTrace());
        this.setCode(code);
    }

    public PfmExceptionCode getCode() {
        return code;
    }

    public void setCode(PfmExceptionCode code) {
        this.code = code;
    }

    public static ResponseEntity<BusinessError> handleException(Exception ex) {
        HttpStatus status;
        String code;
        String message;

        if (ex instanceof PfmException) {
            PfmException pfm = (PfmException) ex;
            status = pfm.code.getHttpStatus();
            code = pfm.getCode().name();
            message = pfm.getMessage();
        } else if (ex instanceof DuplicateKeyException) {

            DuplicateKeyException dk = (DuplicateKeyException) ex;
            status = HttpStatus.BAD_REQUEST;
            code = "DUPLICATE_KEY";
            message = "duplicate field found: " + ex.getMessage().replaceAll(".*\\{ : \\\\\"|\\\\\" }.*", "");
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            code = "UNKNOWN";
            message = ex.getMessage();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        BusinessError businessError = new BusinessError();
        businessError.setError(code);
        businessError.setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
        businessError.setException(ex.getClass().getSimpleName());
        businessError.setMessage(message);
        businessError.setStatus(status.value());
        businessError.setPath("/api");

        return new ResponseEntity<>(businessError, headers, status);
    }
}
