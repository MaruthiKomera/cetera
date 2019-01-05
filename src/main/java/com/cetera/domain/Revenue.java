package com.cetera.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Revenue table
 * Every time user enter revenue data, a new entry will be created
 * Created by danni on 3/23/16.
 */
@Entity
public class Revenue extends BaseDomain {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="SEQUENCE1")
    @SequenceGenerator(name="SEQUENCE1", sequenceName="REVENUE_SEQ", allocationSize=1)
    private Long id;
    private String personId;
    private BigDecimal yearlyRevenue;
    private BigDecimal qualifiedPerc;
    private BigDecimal nonRecurringPerc;
    
    /*
     * The following fields are not necessary to be saved in the database:
     * - nonQualifiedPerc
     * - recurringPerc
     * - iQuotientValue
     * - iQuotientRisk
     * 
     * These values can be easily calculated from the values that are saved.
     * NOTE: Need to confirm whether this is fine to do.
     */

    public Revenue() {}

    public Revenue(BigDecimal yearlyRevenue, BigDecimal qualifiedPerc, BigDecimal nonRecurringPerc) {
        this.yearlyRevenue = yearlyRevenue;
        this.qualifiedPerc = qualifiedPerc;
        this.nonRecurringPerc = nonRecurringPerc;
    }

    public Revenue(Payload payload) {
        this.personId = payload.getUuid();
        this.yearlyRevenue = payload.getGdc();
        this.qualifiedPerc = payload.getPerQual();
        this.nonRecurringPerc = payload.getPerQualNonRecur();
        this.setCreatedOn(new Date());
        this.setCreatedBy(payload.getUuid());
    }

    public BigDecimal getYearlyRevenue() {
        return yearlyRevenue;
    }

    public void setYearlyRevenue(BigDecimal yearlyRevenue) {
        this.yearlyRevenue = yearlyRevenue;
    }

    public BigDecimal getQualifiedPerc() {
        return qualifiedPerc;
    }

    public void setQualifiedPerc(BigDecimal qualifiedPerc) {
        this.qualifiedPerc = qualifiedPerc;
    }

    public BigDecimal getNonRecurringPerc() {
        return nonRecurringPerc;
    }

    public void setNonRecurringPerc(BigDecimal nonRecurringPerc) {
        this.nonRecurringPerc = nonRecurringPerc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }
}
