package com.cetera.domain;

import com.cetera.enums.SystemUser;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.Date;

/**
 * Resources table
 * Created by danni on 4/5/16.
 */
@Entity
public class Resources extends BaseDomain {
    @Id
    private Long id;
    @Lob
    @Basic
    private String links;
    private String version;
    private String description;

    public Resources() {}

    public Resources(Long id, String links, String description, String version) {
        this.id = id;
        this.links = links;
        this.description = description;
        this.version = version;
        this.setCreatedBy(SystemUser.CETERA.name());
        this.setCreatedOn(new Date());
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLinks() {
        return links;
    }

    public void setLinks(String links) {
        this.links = links;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
