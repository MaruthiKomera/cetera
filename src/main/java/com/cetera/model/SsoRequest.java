package com.cetera.model;

/**
 * SSO link Request API model
 * Created by danni on 4/11/16.
 */
public class SsoRequest {
    private String idp;
    private String nameid;
    private String code;
    //ticks
    private Long timestamp;
    private String hvalue;
    private String target;
    private String url;

    public SsoRequest() {}

    public SsoRequest(String idp, String nameid, String code, Long timestamp, String hvalue, String target, String url) {
        this.idp = idp;
        this.nameid = nameid;
        this.code = code;
        this.timestamp = timestamp;
        this.hvalue = hvalue;
        this.target = target;
        this.url = url;
    }

    public String getIdp() {
        return idp;
    }

    public void setIdp(String idp) {
        this.idp = idp;
    }

    public String getNameid() {
        return nameid;
    }

    public void setNameid(String nameid) {
        this.nameid = nameid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getHvalue() {
        return hvalue;
    }

    public void setHvalue(String hvalue) {
        this.hvalue = hvalue;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

