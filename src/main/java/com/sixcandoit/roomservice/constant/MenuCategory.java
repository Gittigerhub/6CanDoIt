package com.sixcandoit.roomservice.constant;

public enum MenuCategory {

    Korean("한식"), Chinese("중식"), Western("양식"), Japanese("일식"), Drink("음료");

    private final String displayName;

    MenuCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
