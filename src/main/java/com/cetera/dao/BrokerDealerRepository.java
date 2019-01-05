package com.cetera.dao;

import com.cetera.domain.BrokerDealer;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Repository for Broker_Dealer table
 * Created by danni on 4/10/16.
 */
public interface BrokerDealerRepository extends PagingAndSortingRepository<BrokerDealer, Long> {
    List<BrokerDealer> findByVersion(String version);
    BrokerDealer findByExternalId(Integer externalId);
}
