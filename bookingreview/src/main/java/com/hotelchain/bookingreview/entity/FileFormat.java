package com.hotelchain.bookingreview.entity;

public enum FileFormat {
    CSV("CSV"),
    JSON("JSON"),
    XML("XML"),
    DOC("DOC");

    private final String displayName;

    FileFormat(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}