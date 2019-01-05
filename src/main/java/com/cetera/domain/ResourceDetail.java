package com.cetera.domain;

import java.util.List;

/**
 * Resources mapping for links in resources table
 * Created by danni on 4/4/16.
 */
public class ResourceDetail {
    private String tabTitle;
    private String pageTitle;
    private String description;
    private List<ResourceStep> resourceStepList;

    public ResourceDetail() {}

    public ResourceDetail(String tabTitle, String pageTitle, String description, List<ResourceStep> resourceStepList) {
        this.tabTitle = tabTitle;
        this.pageTitle = pageTitle;
        this.description = description;
        this.resourceStepList = resourceStepList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ResourceStep> getResourceStepList() {
        return resourceStepList;
    }

    public void setResourceStepList(List<ResourceStep> resourceStepList) {
        this.resourceStepList = resourceStepList;
    }

    public String getTabTitle() {
        return tabTitle;
    }

    public void setTabTitle(String tabTitle) {
        this.tabTitle = tabTitle;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }
}
