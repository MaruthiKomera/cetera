package com.cetera.domain;

/**
 * Resources item mapping inside each resource step
 * Created by danni on 3/29/16.
 */
public class ResourceItem {
    private String type;
    private String url;
    private String description;
    private String sso;
    public ResourceItem() {}

    public ResourceItem(String type, String url, String description, String sso) {
        this.type = type;
        this.url = url;
        this.description = description;
        this.sso = sso;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSso() {
        return sso;
    }

    public void setSso(String sso) {
        this.sso = sso;
    }
}
