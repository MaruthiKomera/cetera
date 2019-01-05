package com.cetera.aspects;

import com.cetera.domain.Answers;
import com.cetera.domain.EmailSession;
import com.cetera.domain.IQuantify;
import com.cetera.domain.Person;
import com.cetera.domain.Template;
import com.cetera.enums.YesOrNo;
import com.cetera.exceptions.PfmException;
import com.cetera.exceptions.PfmExceptionCode;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by danni on 4/22/16.
 */

@Component
public class EmailUtils {
    private static Logger logger = LoggerFactory.getLogger(EmailUtils.class);

    @Value("${mail.smtp.host}")
    private String mailSmtpHost;

    @Value("${mail.smtp.port}")
    private int mailSmtpPort;

    @Value("${mail.smtp.auth}")
    private String mailSmtpAuth;

    @Value("${mail.smtp.user}")
    private String mailSmtpUser;

    @Value("${mail.smtp.password}")
    private String mailSmtpPassword;

    @Value("${mail.bcc.internal}")
    private String mailBccInternal;

    @Value("${prod.mail.send.to.external}")
    private String mailToExternal;

    @Value("${mail.bcc.external}")
    private String mailBccExternal;

    @Value("${mail.reply.to}")
    private String mailReplyTo;

    @Value("${email.template.filePath}")
    private String templateFilePath;

    @Value("${application.env}")
    private String env;

    @Autowired
    private EmailSession emailSession;

    private Configuration freeMarkerConfig;

    @PostConstruct
    public void init() {
        //init free maker
        freeMarkerConfig = new Configuration();
        freeMarkerConfig.setDefaultEncoding("UTF-8");
        freeMarkerConfig.setLocale(Locale.US);
        freeMarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    public EmailUtils() {}

    private void sendMail(List<String> toRecipients, List<String> bccRecipients, InternetAddress from,
                          String subjectText, Multipart multipart) {
        Session session = emailSession.getSession();
        MimeMessage message = new MimeMessage(session);

        try {
            /**
             * Setting the message's headers.
             */
            setMessageHeaders(message, from, toRecipients, bccRecipients, subjectText);

            /**
             * set message's body
             */
            message.setContent(multipart);

            /**
             * Send out the message.
             */
        synchronized(this) {
            Transport transport = session.getTransport("smtp");
            if ("false".equals(mailSmtpAuth)) {
                transport.connect();
            } else {
                transport.connect(mailSmtpHost, mailSmtpPort, mailSmtpUser, mailSmtpPassword);
            }

            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        }

        } catch (AddressException e) {
            logger.debug("Email address error is {}", e.getMessage());
            throw new PfmException("Email address error", PfmExceptionCode.EMAIL_ADDRESS_FORMAT_ERROR, e);
        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.debug("email sending error message is {}", e.getMessage());
            logger.debug("email sending error class is {}", e.getClass().getName());
            throw new PfmException("Error sending email", PfmExceptionCode.EMAIL_SEND_ERROR);
        }
    }

    private void prepareAndSendMail(Template template, List<String> toList, List<String> bccList,
                                    InternetAddress from, Map<String, String> variables) {
        /**
         * Validating the variables to be relevant to the template.
         */
        if (!template.validateVariables(variables)) {
            throw new PfmException("Template variable validation failed",
                PfmExceptionCode.EMAIL_TEMPLATE_VARIABLE_VALIDATION_ERROR);
        }

        /**
         * Substituting any variable text present in the subject string.
         */
        String subjectText = template.getTemplateType().getSubjectText();
        for (Map.Entry<String, String> variableEntry : variables.entrySet()) {
            String searchExpression = "${" + variableEntry.getKey() + "}";
            subjectText = subjectText.replace(searchExpression, variableEntry.getValue());
        }

        /**
         * Preparing the email template using the template file and sending out
         * the email.
         */
        try {
            File templateFile = new ClassPathResource(
                this.templateFilePath + template.getTemplateType().valueOf() + ".ftl").getFile();

            this.freeMarkerConfig.setDirectoryForTemplateLoading(new File(templateFile.getParent()));

            freemarker.template.Template emailTemplate;

            //get template string as body
            emailTemplate = this.freeMarkerConfig.getTemplate(templateFile.getName());
            Writer writer = new StringWriter();
            emailTemplate.process(variables, writer);
            Multipart emailBody = getMessageBody(null, writer.toString());

            sendMail(toList, bccList, from, subjectText, emailBody);
        } catch (IOException | TemplateException e1) {
            logger.debug("Freemarker template file exception. {}", e1.getMessage());
            throw new PfmException("Freemarker template file exception.", PfmExceptionCode.EMAIL_FREEMARKER_EXCEPTION);
        } catch (MessagingException e) {
            logger.debug("Cannot create multipart email body. {}", e.getMessage());
            throw new PfmException("Cannot create multipart email body.", PfmExceptionCode.EMAIL_BODY_NULL);
        }
    }

    /**
     * Formats the Question-Answer Map for conversion to the html format as required
     * by the email template file.
     */
    private String formatQuestionAnswerVariable(List<Answers> answers) {
        StringBuilder questionAnswerString = new StringBuilder();

        for ( int i = 1; i <= answers.size(); i++) {
            questionAnswerString.append("<tr>"); // Start table row
            questionAnswerString.append("<td>"); // Column 1 data
            questionAnswerString.append(Integer.toString(i)).append(". ");
            questionAnswerString.append(answers.get(i - 1).getQuestion());
            questionAnswerString.append("</td>");
            questionAnswerString.append("<td>"); // Column 2 data
            questionAnswerString.append(answers.get(i - 1).getAnswer());
            questionAnswerString.append("</td>");
            questionAnswerString.append("</tr>"); // End table row
        }
        return questionAnswerString.toString();
    }

    /**
     * The assessment completion email should have the following addresses: To:
     * Business Consultant. Bcc: (Specified by Cetera for the automated mail
     * sorting service) From: Internal Advisor. Reply To: No reply.
     *
     */
    @Async
    public void mailAssessmentResults(Person person, List<Answers> answers, IQuantify iQuantify) {
        //1. validate all inputs
        if (person == null) {
            throw new PfmException("Person is null", PfmExceptionCode.PERSON_NULL);
        }

        if (answers == null) {
            throw new PfmException("Answers is null", PfmExceptionCode.ANSWERS_NULL);
        }

        if (iQuantify == null) {
            throw new PfmException("IQuantify is null", PfmExceptionCode.IQUANTIFY_NULL);
        }

        //2. get email variables
        Map<String, String> variables = getEmailVariables(person, iQuantify, answers);

        //3. get email to list
        List<String> toList = getEmailToList(person);
        if (toList == null) {
            return;
        }

        //4. get email bcc list
        List<String> bccList = getEmailBccList(person);

        //5. send email
        InternetAddress from;
        try {
            from = new InternetAddress(person.getEmail(), person.getFirstName() + " " +person.getLastName());
        } catch (UnsupportedEncodingException e) {
            logger.debug("Cannot construct FROM email address. {}", e.getMessage());
            throw new PfmException("Cannot construct FROM email address.", PfmExceptionCode.EMAIL_ADDRESS_FORMAT_ERROR);
        }
        prepareAndSendMail(Template.ASSESSMENT_COMPLETED, toList, bccList, from, variables);
    }

    /**
     * get email variables
     * @param person
     * @param iQuantify
     * @param answers
     * @return
     */
    private Map<String, String> getEmailVariables(Person person, IQuantify iQuantify, List<Answers> answers) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date assessmentDate = new Date();
        String assessmentDateString = dateFormat.format(assessmentDate) + " (UTC)";

        Map<String, String> variables = new HashMap<>();
        variables.put("ADVISOR_NAME", person.getFirstName() + " " + person.getLastName());
        variables.put("ADVISOR_EMAIL", person.getEmail());
        variables.put("I_QUANTIFY_SCORE", Integer.toString(iQuantify.getiQuantify()));
        variables.put("COMPLETED_ON", assessmentDateString);
        variables.put("QUESTIONS_ANSWERS", formatQuestionAnswerVariable(answers));
        return variables;
    }

    /**
     * get email to list
     * @param person
     * @return
     */
    private List<String> getEmailToList(Person person) {
        String internal = person.getIsInternal();
        List<String> toList = new ArrayList<>();
        if(env.equals("DIT") || env.equals("UAT")){
            toList.add("alexandr.trufkin@cetera.com");
            toList.add("albert.cohen@cetera.com");
        } else if (env.equals("PROD")) {
            if (internal.equals(YesOrNo.Y.name())) {
                if (StringUtils.hasText(person.getConsultantEmail())) {
                    toList.add(person.getConsultantEmail());
                } else {
                    return null;
                }
            } else {
                toList.add(mailToExternal);
            }
        } else {
            logger.error("Error: Wrong environment property [{}]", env);
            return null;
        }
        return toList;
    }

    private List<String> getEmailBccList(Person person) {
        String[] bccAddresses;

        bccAddresses = (person.getIsInternal().equals(YesOrNo.Y.name())) ? mailBccInternal.split("\\s*,\\s*")
            : mailBccExternal.split("\\s*,\\s*");
        return new ArrayList<>(Arrays.asList(bccAddresses));
    }

    private Multipart getMessageBody(String plainBodyText, String htmlBodyText) throws MessagingException {
        BodyPart messageBodyPart;
        Multipart multipart = new MimeMultipart("alternative");
        if (plainBodyText != null) {
            messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(plainBodyText);
            multipart.addBodyPart(messageBodyPart);
        }
        if (htmlBodyText != null) {
            messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(htmlBodyText, "text/html");
            multipart.addBodyPart(messageBodyPart);
        }
        return multipart;
    }

    /**
     * This method sets the header values of the message.
     *
     * @author Radwan
     * @param message
     *            - The message object which needs to be configured.
     * @param from
     *            - The sender email address.
     * @param toRecipients
     *            - The recipients of the email.
     * @param bccRecipients
     *            - The BCC recipients of the email.
     * @param subjectText
     *            - The email subject text.
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    private void setMessageHeaders(MimeMessage message, InternetAddress from, List<String> toRecipients,
                                   List<String> bccRecipients, String subjectText) throws MessagingException, UnsupportedEncodingException {

        if (env.equals("DIT") || env.equals("UAT"))
            subjectText = "[" + env + "] " + subjectText;

        message.setSubject(subjectText);
        message.setFrom(from);
        message.setReplyTo(InternetAddress.parse(mailReplyTo));

        /**
         * Set the recipients for this mail.
         */
        for (String recipient : toRecipients) {
            message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        }

        if (bccRecipients != null) {
            for (String recipient : bccRecipients) {
                message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(recipient));
            }
        }
    }
}
