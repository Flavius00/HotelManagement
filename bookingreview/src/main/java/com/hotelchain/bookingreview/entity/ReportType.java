package com.hotelchain.bookingreview.entity;

public enum ReportType {
    BOOKING("Booking Report"),
    REVENUE("Revenue Report"),
    OCCUPANCY("Occupancy Report"),
    CUSTOMER("Customer Report");

    private final String displayName;

    ReportType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
