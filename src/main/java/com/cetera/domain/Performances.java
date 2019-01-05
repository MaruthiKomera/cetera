package com.cetera.domain;

import com.cetera.enums.SystemUser;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Performances seed data table
 * only contains one entry
 * Created by danni on 3/29/16.
 */
@Entity
public class Performances extends BaseDomain {
    @Id
    private Long id;
    private Integer userCount;
    private BigDecimal performance;

    public Performances() {}

    public Performances(Long id, Integer userCount, BigDecimal performance) {
        this.id = id;
        this.userCount = userCount;
        this.performance = performance;
        this.setCreatedBy(SystemUser.CETERA.name());
        this.setCreatedOn(new Date());
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public BigDecimal getPerformance() {
        return performance;
    }

    public void setPerformance(BigDecimal performance) {
        this.performance = performance;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
