package com.cetera.domain;

import java.util.List;

/**
 * Resource step mapping object
 * Created by danni on 3/29/16.
 */
public class ResourceStep {
    private String title;
    private String description;
    private List<ResourceItem> resourceItems;

    public ResourceStep() {}

    public ResourceStep(String title, String description, List<ResourceItem> resourceItems) {
        this.title = title;
        this.description = description;
        this.resourceItems = resourceItems;
    }

    public List<ResourceItem> getResourceItems() {
        return resourceItems;
    }

    public void setResourceItems(List<ResourceItem> resourceItems) {
        this.resourceItems = resourceItems;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
