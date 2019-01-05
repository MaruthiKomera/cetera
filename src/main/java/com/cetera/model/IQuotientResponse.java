package com.cetera.model;

import java.math.BigDecimal;

/**
 * IQuotient Response API model
 * Created by danni on 3/23/16.
 */
public class IQuotientResponse {
    private BigDecimal iQuotientValue;
    private BigDecimal iQuotientRisk;
    private BigDecimal nonQualifiedPerc;
    private BigDecimal recurringPerc;

    public IQuotientResponse() {}

    public BigDecimal getiQuotientValue() {
        return iQuotientValue;
    }

    public void setiQuotientValue(BigDecimal iQuotientValue) {
        this.iQuotientValue = iQuotientValue;
    }

    public BigDecimal getiQuotientRisk() {
        return iQuotientRisk;
    }

    public void setiQuotientRisk(BigDecimal iQuotientRisk) {
        this.iQuotientRisk = iQuotientRisk;
    }

    public BigDecimal getNonQualifiedPerc() {
        return nonQualifiedPerc;
    }

    public void setNonQualifiedPerc(BigDecimal nonQualifiedPerc) {
        this.nonQualifiedPerc = nonQualifiedPerc;
    }

    public BigDecimal getRecurringPerc() {
        return recurringPerc;
    }

    public void setRecurringPerc(BigDecimal recurringPerc) {
        this.recurringPerc = recurringPerc;
    }
}
