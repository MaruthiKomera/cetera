package com.cetera.services;

import com.cetera.domain.EncryptedPayload;

import java.util.Date;
import java.util.List;

/**
 * <i>EncryptedPayloadService</i> is a Service Interface that is used to define
 * the services it provides related to <i>EncryptedPayload</i> class.
 * 
 * @see {@link com.cetera.domain.EncryptedPayload EncryptedPayload}
 * @author Radwan
 *
 */
public interface EncryptedPayloadService {

    /**
     * It saves the received payload string in Cetera's DB.
     * 
     * @param payload
     * @return {@link com.cetera.domain.EncryptedPayload EncryptedPayload}
     */
    EncryptedPayload create(String payload);

    /**
     * It gets a List of <i>{@link com.cetera.domain.EncryptedPayload
     * EncryptedPayload}</i>s from the Cetera's DB where their creation date is
     * after <i>date</i> parameter.
     * 
     * @param date
     * @return
     */
    List<EncryptedPayload> getPayloadsFromDate(Date date);

    /**
     * It gets a List of <i>{@link com.cetera.domain.EncryptedPayload
     * EncryptedPayload}</i> from Cetera's DB where their creation date is
     * between <i>fromDate</i> and <i>toDate</i>.
     * 
     * @param fromDate
     * @param toDate
     * @return
     */
    List<EncryptedPayload> getPayloadsBetween(Date fromDate, Date toDate);

}
