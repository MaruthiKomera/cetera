package com.cetera.domain;

import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * @author sahilshah
 * basic domain for all db entities
 */
@MappedSuperclass
public abstract class BaseDomain {

    private Date createdOn, updatedOn;
    private String createdBy, updatedBy;
    
    public Date getCreatedOn() {
        return createdOn != null ? new Date(createdOn.getTime()) : null;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn != null ? new Date(createdOn.getTime()) : null;
    }

    public Date getUpdatedOn() {
        return updatedOn != null ? new Date(updatedOn.getTime()) : null;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn != null ? new Date(updatedOn.getTime()) : null;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
