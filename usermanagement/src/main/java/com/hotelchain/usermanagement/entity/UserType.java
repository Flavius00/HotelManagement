package com.hotelchain.usermanagement.entity;

public enum UserType {
    CLIENT("Client"),
    EMPLOYEE("Employee"),
    MANAGER("Manager"),
    ADMINISTRATOR("Administrator");

    private final String displayName;

    UserType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}