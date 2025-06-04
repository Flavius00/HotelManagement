package com.hotelchain.usermanagement.entity;

public enum NotificationStatus {
    SENT("Sent"),
    FAILED("Failed"),
    PENDING("Pending");

    private final String displayName;

    NotificationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}