package com.hotelchain.reservationservice.adapter;

import org.springframework.stereotype.Component;

/**
 * Adapter Pattern Implementation
 * Adapts external service responses to our internal format
 */

// Target interface - what our application expects
interface NotificationService {
    boolean sendEmail(String recipient, String subject, String message);
    boolean sendSMS(String phoneNumber, String message);
}

// Adaptee - External Email Service
class ExternalEmailService {
    public void sendEmail(String to, String subject, String body) {
        // Simulate external email service
        System.out.println("External Email Service:");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
    }
}

// Adaptee - External SMS Service
class ExternalSMSGateway {
    public String sendSMS(String phoneNumber, String text) {
        // Simulate external SMS gateway
        System.out.println("SMS Gateway:");
        System.out.println("Phone: " + phoneNumber);
        System.out.println("Message: " + text);
        return "SMS_SENT_OK";
    }
}

// Adapter - Adapts external services to our NotificationService interface
@Component
public class ExternalServiceAdapter implements NotificationService {

    private final ExternalEmailService emailService;
    private final ExternalSMSGateway smsGateway;

    public ExternalServiceAdapter() {
        this.emailService = new ExternalEmailService();
        this.smsGateway = new ExternalSMSGateway();
    }

    @Override
    public boolean sendEmail(String recipient, String subject, String message) {
        try {
            emailService.sendEmail(recipient, subject, message);
            return true;
        } catch (Exception e) {
            System.err.println("Email sending failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean sendSMS(String phoneNumber, String message) {
        try {
            String result = smsGateway.sendSMS(phoneNumber, message);
            return "SMS_SENT_OK".equals(result);
        } catch (Exception e) {
            System.err.println("SMS sending failed: " + e.getMessage());
            return false;
        }
    }

    // Additional convenience methods specific pentru reviews
    public boolean sendReviewConfirmation(String email, String reviewDetails) {
        String subject = "Review Posted - HotelChain";
        String message = "Thank you for your review!\n\n" + reviewDetails;
        return sendEmail(email, subject, message);
    }

    public boolean sendReviewModerationNotification(String email, String reviewDetails, boolean approved) {
        String subject = approved ? "Review Approved - HotelChain" : "Review Moderated - HotelChain";
        String message = approved
                ? "Your review has been approved and is now visible to other users.\n\n" + reviewDetails
                : "Your review has been moderated by our team.\n\n" + reviewDetails;
        return sendEmail(email, subject, message);
    }
}