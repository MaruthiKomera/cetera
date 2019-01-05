package com.cetera.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Encrypted payload object from cetera website
 * Created by danni on 3/30/16.
 */
public class Payload {
    private String uuid;
    private Date timestamp;
    private Integer bd;
    private String firstName;
    private String lastName;
    private String email;
    private BigDecimal gdc;
    private BigDecimal perQual;
    private BigDecimal perQualNonRecur;
    private String busConsEmail;

    public Payload() {}


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getBd() {
        return bd;
    }

    public void setBd(Integer bd) {
        this.bd = bd;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public BigDecimal getGdc() {
        return gdc;
    }

    public void setGdc(BigDecimal gdc) {
        this.gdc = gdc;
    }

    public BigDecimal getPerQual() {
        return perQual;
    }

    public void setPerQual(BigDecimal perQual) {
        this.perQual = perQual;
    }

    public BigDecimal getPerQualNonRecur() {
        return perQualNonRecur;
    }

    public void setPerQualNonRecur(BigDecimal perQualNonRecur) {
        this.perQualNonRecur = perQualNonRecur;
    }

    public String getBusConsEmail() {
        return busConsEmail;
    }

    public void setBusConsEmail(String busConsEmail) {
        this.busConsEmail = busConsEmail;
    }
}
