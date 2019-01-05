package com.cetera.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.cetera.domain.EncryptedPayload;

/**
 * A repository interface for the <i>EncryptedPayload</i> Domain class.
 * 
 * @see {@link com.cetera.domain.EncryptedPayload EncryptedPayload}
 * @author Radwan
 *
 */
public interface EncryptedPayloadRepository extends PagingAndSortingRepository<EncryptedPayload, Long> {

    /**
     * Returns a list of payloads which their <i>createdOn</i> property is
     * greater than <i>Date</i> parameter.
     * 
     * @param date
     * @return List<{@link com.cetera.domain.EncryptedPayload EncryptedPayload}>
     */
    List<EncryptedPayload> findByCreatedOnGreaterThan(Date date);

    /**
     * Returns a list of payloads which their <i>createdOn</i> property is
     * between <i>fromDate</i> and <i>toDate</i> parameters.
     * 
     * @param fromDate
     * @param toDate
     * @return List<{@link com.cetera.domain.EncryptedPayload EncryptedPayload}>
     *         where (fromDate <
     *         {@link com.cetera.domain.EncryptedPayload#createdOn createdOn} <
     *         toDate).
     */
    List<EncryptedPayload> findByCreatedOnBetween(Date fromDate, Date toDate);

}