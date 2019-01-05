package com.cetera.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.Session;
import java.util.Properties;

/**
 * Created by danni on 5/2/16.
 */
@Component
public class EmailSession {
    @Value("${mail.smtp.starttls.enable}")
    private String mailSmtpStarttlsEnable;

    @Value("${mail.smtp.auth}")
    private String mailSmtpAuth;

    @Value("${mail.smtp.host}")
    private String mailSmtpHost;

    @Value("${mail.smtp.port}")
    private String mailSmtpPort;

    private static Session session;

    private static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    private static final String MAIL_SMTP_HOST = "mail.smtp.host";
    private static final String MAIL_SMTP_USER = "mail.smtp.user";
    private static final String MAIL_SMTP_PASSWORD = "mail.smtp.password";
    private static final String MAIL_SMTP_PORT = "mail.smtp.port";
    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";

    @PostConstruct
    public void init() {
        //init mail session
        Properties props = new Properties();

        props.put(MAIL_SMTP_STARTTLS_ENABLE, mailSmtpStarttlsEnable);
        props.put(MAIL_SMTP_AUTH, mailSmtpAuth);
        if ("false".equals(mailSmtpAuth)) {
            props.put(MAIL_SMTP_HOST, mailSmtpHost);
            props.put(MAIL_SMTP_PORT, mailSmtpPort);
        }
        //remove debug for production
        //props.put("mail.debug", "true");
        session = Session.getInstance(props, null);
    }

    public Session getSession() {
        return session;
    }
}
