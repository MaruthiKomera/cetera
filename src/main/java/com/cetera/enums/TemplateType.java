package com.cetera.enums;

import java.util.HashSet;
import java.util.Set;

/**
 * Email template type
 * Created by sahil on 3/6/16.
 */
public enum TemplateType {
    /**
     * register is not used for now
     */
    ASSESSMENT_COMPLETED("ASSESSMENT_COMPLETED", "iQuantify Assessment completed by ${ADVISOR_NAME}");


    private String type;
    
    private String subjectText;

    TemplateType(String type, String subject) {
        this.type = type;
        this.subjectText = subject;
    }

    private static final Set<String> VALUE_SET = new HashSet<String>() {
        {
            for (TemplateType type : values()) {
                add(type.toString());
            }
        }
    };

    public String valueOf() {
        return type;
    }

    public static final Set<String> valuesSet() {
        return VALUE_SET;
    }

    public String getSubjectText() {
        return subjectText;
    }
}
