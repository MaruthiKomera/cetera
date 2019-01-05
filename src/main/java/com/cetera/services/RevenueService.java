package com.cetera.services;

import com.cetera.domain.Revenue;
import com.cetera.model.IQuotientResponse;

import java.math.BigDecimal;

/**
 * This service is used to manage revenue data per request
 * Created by danni on 3/23/16.
 */
public interface RevenueService {
    IQuotientResponse calculateIQuotient(BigDecimal yearlyRevenue,
                                BigDecimal qualifiedPerc,
                                BigDecimal nonRecurringPerc);
    
    void create(Revenue revenue);
    void validateRevenue(Revenue revenue);
}
