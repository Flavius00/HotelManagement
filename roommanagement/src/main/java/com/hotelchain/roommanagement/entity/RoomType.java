package com.hotelchain.roommanagement.entity;

public enum RoomType {
    SINGLE("Single Room"),
    DOUBLE("Double Room"),
    SUITE("Suite"),
    DELUXE("Deluxe Room"),
    FAMILY("Family Room");

    private final String displayName;

    RoomType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}