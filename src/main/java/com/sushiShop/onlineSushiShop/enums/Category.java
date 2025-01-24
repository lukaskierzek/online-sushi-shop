package com.sushiShop.onlineSushiShop.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Category {
    NEW_ITEMS("New items");

    private final String value;

    Category(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
