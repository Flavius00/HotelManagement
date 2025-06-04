package com.hotelchain.roommanagement.entity;

public enum RoomPosition {
    GROUND_FLOOR("Ground Floor"),
    FIRST_FLOOR("First Floor"),
    SECOND_FLOOR("Second Floor"),
    THIRD_FLOOR("Third Floor"),
    TOP_FLOOR("Top Floor");

    private final String displayName;

    RoomPosition(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}