package com.hotelchain.userservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SmsService {

    private final RestTemplate restTemplate;

    @Value("${sms.api.url:https://www.smsadvert.ro/api}")
    private String apiUrl;

    @Value("${sms.api.key:your-api-key}")
    private String apiKey;

    public SmsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Trimite SMS folosind SmsAdvert API
     */
    public boolean sendSMS(String phoneNumber, String message) {
        try {
            // Formatează numărul de telefon
            String formattedPhone = formatPhoneNumber(phoneNumber);

            if (formattedPhone == null) {
                log.warn("Invalid phone number: {}", phoneNumber);
                return false;
            }

            String escapedPhone = formattedPhone.replace("\"", "\\\"");
            String escapedMessage = message.replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t")
                    .replace("\\", "\\\\");

            // Hardcode JSON body as string
            String jsonBody = String.format(
                    "{\"phone\":\"%s\",\"shortTextMessage\":\"%s\"}",
                    escapedPhone,
                    escapedMessage
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", apiKey);

            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

            String endpoint = apiUrl + "/sms/";
            log.info("Sending SMS to {} via SmsAdvert API", formattedPhone);

            ResponseEntity<String> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            log.info("SmsAdvert API response status: {}", response.getStatusCode());

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("SMS sent successfully to: {}", formattedPhone);
                return true;
            } else {
                log.error("SmsAdvert API returned error status: {} for phone: {}",
                        response.getStatusCode(), formattedPhone);
                return false;
            }

        } catch (Exception e) {
            log.error("Error sending SMS to: {} via SmsAdvert API", formatPhoneNumber(phoneNumber), e);
            return false;
        }
    }

    /**
     * Formatează numărul de telefon pentru România
     */
    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return null;
        }

        // Elimină spațiile și caracterele speciale
        String cleaned = phoneNumber.replaceAll("[^0-9+]", "");

        if (cleaned.isEmpty()) {
            return null;
        }

        // Dacă începe cu 0, înlocuiește cu +40
        if (cleaned.startsWith("0")) {
            cleaned = "+40" + cleaned.substring(1);
        }

        // Dacă începe cu 40, adaugă +
        if (cleaned.startsWith("40") && !cleaned.startsWith("+40")) {
            cleaned = "+" + cleaned;
        }

        // Dacă nu are prefix și are 10 cifre, presupunem că e număr românesc
        if (!cleaned.startsWith("+") && cleaned.length() == 10) {
            cleaned = "+40" + cleaned;
        }

        // Validează lungimea finală (ar trebui să fie +40 + 9 cifre = 12 caractere)
        if (cleaned.startsWith("+40") && cleaned.length() != 12) {
            log.warn("Invalid Romanian phone number length: {}", cleaned);
            return null;
        }

        return cleaned;
    }

    /**
     * Validează dacă numărul de telefon este valid
     */
    public boolean isValidPhoneNumber(String phoneNumber) {
        String formatted = formatPhoneNumber(phoneNumber);
        return formatted != null;
    }
}