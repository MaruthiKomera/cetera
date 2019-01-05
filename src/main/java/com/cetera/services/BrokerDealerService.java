package com.cetera.services;

import com.cetera.domain.BrokerDealer;

/**
 * This service is used to create and get Broker dealer info
 * Created by danni on 4/10/16.
 */
public interface BrokerDealerService {
    void create(String dataSeededAsOfDate);
    BrokerDealer findByExternalId(Integer id);
    BrokerDealer findOne(Long id);
}
