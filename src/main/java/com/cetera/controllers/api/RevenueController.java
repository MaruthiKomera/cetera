package com.cetera.controllers.api;

import com.cetera.model.IQuotientResponse;
import com.cetera.services.RevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Created by danni on 3/23/16.
 */
@RestController
@RequestMapping("/api/revenue")
public class RevenueController {

    @Autowired
    private RevenueService revenueService;

    /**
     * calculate iQuotient and risk value
     * @param auth
     * @param yearlyRevenue
     * @param qualifiedPerc
     * @param nonRecurringPerc
     * @return IQuotientResponse
     */
    @RequestMapping(method = RequestMethod.GET)
    public IQuotientResponse calculate(@RequestHeader("X-CS-Auth") String auth,
                         @RequestParam(value = "yearlyRevenue") BigDecimal yearlyRevenue,
                         @RequestParam(value = "qualifiedPerc") BigDecimal qualifiedPerc,
                         @RequestParam(value = "nonRecurringPerc") BigDecimal nonRecurringPerc) {
        return revenueService.calculateIQuotient(yearlyRevenue, qualifiedPerc, nonRecurringPerc);
    }
}
