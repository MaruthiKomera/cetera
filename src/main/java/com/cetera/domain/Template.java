package com.cetera.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cetera.enums.TemplateType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Email template
 * Created by sahil on 3/6/16.
 */
public class Template {
    private TemplateType templateType;
    private Set<String> variables;

    private Template() {}
    private static Logger logger = LoggerFactory.getLogger(Template.class);

    public static final Template ASSESSMENT_COMPLETED = new Template() {{
        setTemplateType(com.cetera.enums.TemplateType.ASSESSMENT_COMPLETED);
        setVariables(new HashSet<String>() {{
            add("ADVISOR_NAME");
            add("ADVISOR_EMAIL");
            add("I_QUANTIFY_SCORE");
            add("COMPLETED_ON");
            add("QUESTIONS_ANSWERS");
        }});
    }};

    public TemplateType getTemplateType() {
        return templateType;
    }

    public void setTemplateType(TemplateType templateType) {
        this.templateType = templateType;
    }

    /**
     * get the template slug
     * @param acctId
     * @return
     */
    public String getSlug(String acctId) {
        if (acctId != null) {
            return "acct-" + acctId + "-" + templateType.valueOf();
        } else {
            return templateType.valueOf();
        }
    }

    public Set<String> getVariables() {
        return variables;
    }

    public void setVariables(Set<String> variables) {
        this.variables = variables;
    }

    public boolean validateVariables(Map<String, String> map) {
        for (String key : variables) {
            if (!map.containsKey(key)) {
                logger.error("missing variable {} in variable values", key);
                return false;
            }
        }

        return true;
    }
}
