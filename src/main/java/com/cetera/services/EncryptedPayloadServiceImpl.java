package com.cetera.services;

import com.cetera.dao.EncryptedPayloadRepository;
import com.cetera.domain.EncryptedPayload;
import com.cetera.exceptions.PfmException;
import com.cetera.exceptions.PfmExceptionCode;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/**
 *
 * @author Radwan
 *
 */
@Service
public class EncryptedPayloadServiceImpl implements EncryptedPayloadService {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(EncryptedPayloadServiceImpl.class);

	@Autowired
	private EncryptedPayloadRepository payloadRepository;

    @Override
    public EncryptedPayload create(String payload) {

        // Check if the payload is null or empty.
        if (payload == null || payload.isEmpty())
            throw new PfmException("Payload is [NULL] or Empty.", PfmExceptionCode.GENERIC);

        EncryptedPayload savedPayload = new EncryptedPayload(payload, new Date());

        return payloadRepository.save(savedPayload);
    }

    @Override
    public List<EncryptedPayload> getPayloadsFromDate(Date date) {

        return payloadRepository.findByCreatedOnGreaterThan(date);
    }

    @Override
    public List<EncryptedPayload> getPayloadsBetween(Date fromDate, Date toDate) {
        return payloadRepository.findByCreatedOnBetween(fromDate, toDate);
    }
}
