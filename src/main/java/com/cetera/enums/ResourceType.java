package com.cetera.enums;

/**
 * Created by danni on 5/25/16.
 */
public enum ResourceType {
    PDF("pdf"),
    ICAL("ical"),
    EMAIL("email"),
    PHONE("phone"),
    VIDEO("video"),
    WEB("web"),
    EXCEL("excel"),
    WORD("word");

    private String content;
    private ResourceType(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public static boolean contains(String content) {
        for (ResourceType c : ResourceType.values()) {
            if (c.getContent().equals(content)) {
                return true;
            }
        }
        return false;
    }
}
