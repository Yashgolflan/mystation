/*
 * 
 */
package com.stayprime.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class EmailUtil {
    private static final Logger log = LoggerFactory.getLogger(EmailUtil.class);

    public static String sendEmail(String toAddr, String subject, String message, boolean html) {
        log.debug("Sending email: ");
        String reponse;
        try {
            Properties emailProps = createEmailProperties();
            Authenticator auth = (Authenticator) emailProps.get("auth");
            String fromAddress = emailProps.getProperty("from");
            InternetAddress[] toAddress = getEmailAddresses(toAddr);
            Session session = Session.getInstance(emailProps, auth);
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromAddress));
            msg.setRecipients(Message.RecipientType.TO, toAddress);
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            if (html) {
                msg.setContent(message, "text/html");
            }
            else {
                msg.setContent(message, "text/plain");
            }
            Transport.send(msg);
            reponse = "Email sent successfully to " + ArrayUtils.toString(toAddress);
        }
        catch (MessagingException me) {
            log.debug("Error sending email: " + me);
            reponse = me.getMessage();
        }
        catch (Exception e) {
            log.debug("Error sending email: " + e);
            log.trace("Error sending email: ", e);
            reponse = e.getMessage();
        }
        return reponse;
    }

    public static Properties createEmailProperties() {
        Properties emailProps = new Properties();
        final String from = "stayprime@stayprime.com";
        final String pwd = "Dubai57145";
        emailProps.put("mail.smtp.host", "smtp.gmail.com");
        emailProps.put("mail.smtp.ssl.enable", "true");
        emailProps.put("mail.smtp.socketFactory.port", "465");
        emailProps.put("mail.smtp.auth", "true");
        emailProps.put("mail.smtp.port", "465");
        emailProps.put("from", from);
        Authenticator auth = new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, pwd);
            }
        };
        emailProps.put("auth", auth);
        return emailProps;
    }

    public static InternetAddress[] getEmailAddresses(String toAddr) {
        String[] toAddrSplit = toAddr.split("\\s*(=>|,|\\s)\\s*");
        List<InternetAddress> to = new ArrayList<InternetAddress>(toAddrSplit.length);
        for (int i = 0; i < toAddrSplit.length; i++) {
            String trimAddr = StringUtils.trim(toAddrSplit[i]);
            try {
                to.add(new InternetAddress(trimAddr));
            }
            catch (AddressException ex) {
                log.warn(ex.toString());
            }
        }
        return to.toArray(new InternetAddress[to.size()]);
    }
    
}
