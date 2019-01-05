package com.cetera.enums;

/**
 * Created by danni on 5/25/16.
 */
public enum AnswerType {
    DROPDOWN("dropdown"),
    RADIO_BUTTON("radio button"),
    MULTI_SELECT("multi select");

    private String content;

    private AnswerType(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public static boolean contains(String content) {
        for (AnswerType c : AnswerType.values()) {
            if (c.getContent().equals(content)) {
                return true;
            }
        }
        return false;
    }

}
