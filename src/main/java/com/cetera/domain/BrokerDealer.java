package com.cetera.domain;

import com.cetera.enums.SystemUser;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by danni on 4/10/16.
 * Keeps broker dealer info
 */
@Entity
public class BrokerDealer extends BaseDomain {
    @Id
    private Long id;
    private Integer externalId;
    private String bdName;
    private String domainName;
    private String version;

    public BrokerDealer() {}

    public BrokerDealer(Long id, Integer externalId, String bdName, String domainName, String version) {
        this.id = id;
        this.externalId = externalId;
        this.bdName = bdName;
        this.domainName = domainName;
        this.version = version;
        this.setCreatedBy(SystemUser.CETERA.name());
        this.setCreatedOn(new Date());
    }

    public Integer getExternalId() {
        return externalId;
    }

    public void setExternalId(Integer externalId) {
        this.externalId = externalId;
    }

    public String getBdName() {
        return bdName;
    }

    public void setBdName(String bdName) {
        this.bdName = bdName;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
