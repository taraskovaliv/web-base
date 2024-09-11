package dev.kovaliv.services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;

import java.util.Properties;

import static jakarta.mail.Message.RecipientType.TO;

@Log4j2
public class EmailService {

    public static void sendEmail(String to, String subject, String html) {
        new Thread(() -> {
            try {
                Message message = new MimeMessage(getSession());
                message.setFrom(new InternetAddress(System.getenv("EMAIL"), "Запитай мене. ASK ME."));
                message.setRecipients(TO, InternetAddress.parse(to));
                message.setSubject(subject);
                message.setContent(html, "text/HTML; charset=UTF-8");

                Transport.send(message);
            } catch (Exception e) {
                log.warn("Failed to send email to '{}'", to, e);
                throw new RuntimeException(e);
            }
        }).start();
    }

    private static Session getSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtppro.zoho.eu");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        String from = System.getenv("EMAIL");
        if (from == null || from.isEmpty()) {
            log.error("'EMAIL' environment variable is not set");
            throw new RuntimeException("Email is not set");
        }
        String password = System.getenv("EMAIL_PASSWORD");
        if (password == null || password.isEmpty()) {
            log.error("'EMAIL_PASSWORD' environment variable is not set");
            throw new RuntimeException("Email password is not set");
        }

        return Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });
    }
}
