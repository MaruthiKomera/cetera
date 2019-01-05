package com.cetera.services;

import com.cetera.dao.RevenueRepository;
import com.cetera.domain.Revenue;
import com.cetera.exceptions.PfmException;
import com.cetera.exceptions.PfmExceptionCode;
import com.cetera.model.IQuotientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * Created by danni on 3/23/16.
 */
@Service
public class RevenueServiceImpl implements RevenueService {
    private static Logger logger = LoggerFactory.getLogger(RevenueServiceImpl.class);


    @Autowired
    private RevenueRepository revenueRepository;

    private static final int ROUNDING_SCALE = 0;

    /**
     * Calculate iQuotient value
     * @param yearlyRevenue
     * @param qualifiedPerc
     * @param nonRecurringPerc
     * @return
     */
    @Override
    public IQuotientResponse calculateIQuotient(BigDecimal yearlyRevenue,
                                       BigDecimal qualifiedPerc,
                                       BigDecimal nonRecurringPerc) {

        Revenue revenue = new Revenue(yearlyRevenue, qualifiedPerc, nonRecurringPerc);
        validateRevenue(revenue);
        /*
         * Calculating the parameters which can be inferred directly
         * 
         * Formulae:
         * nonQualifiedPerc = 100 - qualifiedPerc
         * recurringPerc = 100 - nonRecurringPerc
         */
        BigDecimal nonQualifiedPerc = new BigDecimal(100).subtract(qualifiedPerc)
            .setScale(ROUNDING_SCALE, RoundingMode.HALF_EVEN);
        BigDecimal recurringPerc = new BigDecimal(100).subtract(nonRecurringPerc)
            .setScale(ROUNDING_SCALE, RoundingMode.HALF_EVEN);
        
        /*
         * Calculating the iQuotient.
         * Units: Dollars
         * 
         * Formulae:
         * iQuotientValue = yearlyRevenue * (qualifiedPerc/100) * (nonRecurringPerc/100)
         * or
         * iQuotientValue = (yearlyRevenue * qualifiedPerc * nonRecurringPerc) / 10000
         */
        BigDecimal iQuotientValue = yearlyRevenue.multiply(qualifiedPerc).multiply(nonRecurringPerc)
            .divide(new BigDecimal(10000), ROUNDING_SCALE, RoundingMode.HALF_EVEN);
        
        /*
         * Calculating the risk percentage.
         * This math needs to be confirmed. Change if required.
         * 
         * Formulae:
         * iQuotientRisk = (iQuotientValue/yearlyRevenue) * 100
         */
        BigDecimal iQuotientRisk = (yearlyRevenue.compareTo(BigDecimal.ZERO) == 0)
            ? BigDecimal.ZERO : iQuotientValue.multiply(new BigDecimal(100))
            .divide(yearlyRevenue, ROUNDING_SCALE, RoundingMode.HALF_EVEN);

        /*
         * Since iQuotientValue and iQuotientRisk are representational figures, they are
         * rounded to the closest integer values.
         */
        IQuotientResponse response = new IQuotientResponse();
        response.setiQuotientRisk(iQuotientRisk);
        response.setiQuotientValue(iQuotientValue);
        response.setNonQualifiedPerc(nonQualifiedPerc);
        response.setRecurringPerc(recurringPerc);
        return response;
    }

    /**
     * Save revenue data to db
     * @param revenue
     */
    @Override
    public void create(Revenue revenue) {
        
        if(revenue == null) {
            throw new PfmException("Revenue argument is null, so cannot create a database entry",
                    PfmExceptionCode.REVENUE_NULL);
        }
        
        revenue.setCreatedOn(new Date());
        revenue.setCreatedBy(revenue.getPersonId());

        revenueRepository.save(revenue);
    }

    @Override
    public void validateRevenue(Revenue revenue) {
        BigDecimal yearlyRevenue = revenue.getYearlyRevenue();
        BigDecimal qualifiedPerc = revenue.getQualifiedPerc();
        BigDecimal nonRecurringPerc = revenue.getNonRecurringPerc();
        /*
         * Verifying the presence of the mandatory fields.
         * - yearlyRevenue
         * - qualifiedPerc
         * - nonRecurringPerc
         */
        if (yearlyRevenue == null) {
            throw new PfmException("Missing revenue for the iQuotient calculation is null ",
                PfmExceptionCode.REVENUE_MISSING_REVENUE);
        }
        if (yearlyRevenue.compareTo(BigDecimal.ZERO) < 0) {
            throw new PfmException("Yearly revenue should be larger than 0.", PfmExceptionCode.REVENUE_WRONG_REVENUE);
        }
        if (qualifiedPerc == null) {
            throw new PfmException("Missing qualified perc for the iQuotient calculation is null ",
                PfmExceptionCode.REVENUE_MISSING_QUALIFIED_PERC);
        }
        if (qualifiedPerc.compareTo(BigDecimal.ZERO) < 0 || qualifiedPerc.compareTo(new BigDecimal(100)) > 0) {
            throw new PfmException("Qualified percentage should be between 0 and 100.",
                PfmExceptionCode.REVENUE_WRONG_QUAL_PERC);
        }
        if (nonRecurringPerc == null) {
            throw new PfmException("Missing non recurring perc for the iQuotient calculation is null ",
                PfmExceptionCode.REVENUE_MISSING_NONRECUR_PERC);
        }
        if (nonRecurringPerc.compareTo(BigDecimal.ZERO) < 0 || nonRecurringPerc.compareTo(new BigDecimal(100)) > 0) {
            throw new PfmException("Non recurring percentage should be between 0 and 100.",
                PfmExceptionCode.REVENUE_WRONG_NON_RECUR);
        }
    }
}
