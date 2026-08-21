package com.civilsupplies.api.service;

import com.civilsupplies.api.entity.Enquiry;
import com.civilsupplies.api.entity.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@civilsupplies.in}")
    private String mailFrom;

    @Value("${app.mail.admin-notify-to:admin@civilsupplies.in}")
    private String adminNotifyTo;

    public EmailService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendEnquiryNotification(Enquiry enquiry) {
        if (mailSender == null) {
            log.info("JavaMailSender not configured. Skipping email notification for Enquiry #{}", enquiry.getId());
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(adminNotifyTo);
            message.setSubject("New Contact Enquiry: " + enquiry.getName());
            message.setText(String.format(
                "You have received a new enquiry:\n\n" +
                "Name: %s\n" +
                "Email: %s\n" +
                "Phone: %s\n" +
                "City: %s\n" +
                "Project Type: %s\n" +
                "Materials: %s\n" +
                "Quantity: %s\n" +
                "Message: %s\n",
                enquiry.getName(), enquiry.getEmail(), enquiry.getPhone(),
                enquiry.getCity(), enquiry.getProjectType(), enquiry.getMaterials(),
                enquiry.getQuantity(), enquiry.getMessage()
            ));

            mailSender.send(message);
            log.info("Sent enquiry notification email for Enquiry #{}", enquiry.getId());
        } catch (Exception e) {
            log.error("Failed to send enquiry email notification: {}", e.getMessage());
        }
    }

    @Async
    public void sendQuoteNotification(Quote quote) {
        if (mailSender == null) {
            log.info("JavaMailSender not configured. Skipping email notification for Quote #{}", quote.getId());
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(adminNotifyTo);
            message.setSubject("New Quote Request (RFQ): " + quote.getName());
            message.setText(String.format(
                "You have received a new Request for Quote:\n\n" +
                "Name: %s\n" +
                "Email: %s\n" +
                "Phone: %s\n" +
                "Site Location: %s\n" +
                "Timeline: %s\n" +
                "Project Details: %s\n" +
                "BOQ File: %s\n",
                quote.getName(), quote.getEmail(), quote.getPhone(),
                quote.getSiteLocation(), quote.getTimeline(), quote.getProjectDetails(),
                quote.getBoqFilename() != null ? quote.getBoqFilename() : "None attached"
            ));

            mailSender.send(message);
            log.info("Sent quote notification email for Quote #{}", quote.getId());
        } catch (Exception e) {
            log.error("Failed to send quote email notification: {}", e.getMessage());
        }
    }
}
