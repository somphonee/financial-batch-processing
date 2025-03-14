package com.financialsystem.batchprocessing.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final JavaMailSender emailSender;

    public NotificationService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }
    public void sendErrorNotification(String jobName, String errorDetails) {
        logger.info("Sending error notification for job: {}", jobName);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("admin@financialsystem.com");
        message.setSubject("Batch Job Error: " + jobName);
        message.setText("The following error occurred during batch processing:\n\n" + errorDetails);

        try {
            emailSender.send(message);
            logger.info("Error notification sent successfully");
        } catch (Exception e) {
            logger.error("Failed to send error notification", e);
        }
    }
    public void sendJobCompletionNotification(String jobName, String statistics) {
        logger.info("Sending completion notification for job: {}", jobName);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("finance@financialsystem.com");
        message.setSubject("Batch Job Completed: " + jobName);
        message.setText("The batch job completed successfully.\n\nStatistics:\n" + statistics);

        try {
            emailSender.send(message);
            logger.info("Completion notification sent successfully");
        } catch (Exception e) {
            logger.error("Failed to send completion notification", e);
        }
    }
}
