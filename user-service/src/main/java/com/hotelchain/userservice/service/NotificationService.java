package com.hotelchain.userservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class NotificationService {

    private final RestTemplate restTemplate;
    private final EmailService emailService;
    private final SmsService smsService;

    @Value("${sms.api.url:https://smsadvert.ro/api}")
    private String smsApiUrl;

    @Value("${sms.api.key:your-sms-api-key}")
    private String smsApiKey;

    public NotificationService(RestTemplate restTemplate, EmailService emailService, SmsService smsService) {
        this.restTemplate = restTemplate;
        this.emailService = emailService;
        this.smsService = smsService;
    }

    /**
     * Trimite email de notificare către utilizator
     */
    public void sendUserUpdateEmail(String userEmail, String username, String changeDescription) {
        try {
            String subject = "Account Update Notification - HotelChain";
            String htmlContent = buildEmailContent(username, changeDescription);

            emailService.sendHtmlEmail(userEmail, subject, htmlContent);

            log.info("Email notification sent successfully to: {}", userEmail);
        } catch (Exception e) {
            log.error("Failed to send email notification to: {}", userEmail, e);
        }
    }

    /**
     * Trimite SMS de notificare către utilizator
     */
    public void sendUserUpdateSMS(String phoneNumber, String username, String changeDescription) {
        try {
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                log.warn("Phone number is empty for user: {}", username);
                return;
            }

            String message = buildSMSContent(username, changeDescription);
            smsService.sendSMS(phoneNumber, message);

            log.info("SMS notification sent successfully to: {}", phoneNumber);
        } catch (Exception e) {
            log.error("Failed to send SMS notification to: {}", phoneNumber, e);
        }
    }

    /**
     * Trimite notificări complete (email + SMS)
     */
    public void sendUserUpdateNotifications(String userEmail, String phoneNumber, String username, String changeDescription) {
        // Trimitere email
        if (userEmail != null && !userEmail.trim().isEmpty()) {
            sendUserUpdateEmail(userEmail, username, changeDescription);
        }

        // Trimitere SMS
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            sendUserUpdateSMS(phoneNumber, username, changeDescription);
        }
    }

    /**
     * Construiește conținutul email-ului
     */
    private String buildEmailContent(String username, String changeDescription) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Account Update</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #2563eb; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .footer { padding: 15px; text-align: center; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>HotelChain</h1>
                        <h2>Account Update Notification</h2>
                    </div>
                    <div class="content">
                        <p>Hello <strong>%s</strong>,</p>
                        <p>Your account has been updated with the following changes:</p>
                        <div style="background-color: white; padding: 15px; border-left: 4px solid #2563eb; margin: 15px 0;">
                            <p><strong>Changes made:</strong> %s</p>
                        </div>
                        <p>If you have any questions about these changes, please contact our support team.</p>
                        <p>Best regards,<br>The HotelChain Team</p>
                    </div>
                    <div class="footer">
                        <p>This is an automated message. Please do not reply to this email.</p>
                        <p>&copy; 2025 HotelChain. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, username, changeDescription);
    }

    /**
     * Construiește conținutul SMS-ului
     */
    private String buildSMSContent(String username, String changeDescription) {
        return String.format(
                "Hello %s! Your HotelChain account has been updated: %s",
                username, changeDescription
        );
    }

    /**
     * Generează descrierea modificărilor
     */
    public String generateChangeDescription(Map<String, Object> changes) {
        if (changes.isEmpty()) {
            return "Account information updated";
        }

        StringBuilder description = new StringBuilder();

        if (changes.containsKey("username")) {
            description.append("Username changed");
        }
        if (changes.containsKey("email")) {
            if (description.length() > 0) description.append(", ");
            description.append("Email updated");
        }
        if (changes.containsKey("phone")) {
            if (description.length() > 0) description.append(", ");
            description.append("Phone number updated");
        }
        if (changes.containsKey("role")) {
            if (description.length() > 0) description.append(", ");
            description.append("Role changed to ").append(changes.get("role"));
        }
        if (changes.containsKey("hotelId")) {
            if (description.length() > 0) description.append(", ");
            description.append("Hotel assignment updated");
        }
        if (changes.containsKey("active")) {
            if (description.length() > 0) description.append(", ");
            description.append("Account status changed to ")
                    .append((Boolean) changes.get("active") ? "Active" : "Inactive");
        }
        if (changes.containsKey("password")) {
            if (description.length() > 0) description.append(", ");
            description.append("Password updated");
        }

        return description.toString();
    }
}