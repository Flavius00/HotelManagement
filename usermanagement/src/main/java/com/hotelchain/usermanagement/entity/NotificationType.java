package com.hotelchain.usermanagement.entity;

public enum NotificationType {
    EMAIL("Email"),
    SMS("SMS"),
    WHATSAPP("WhatsApp"),
    SKYPE("Skype");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}