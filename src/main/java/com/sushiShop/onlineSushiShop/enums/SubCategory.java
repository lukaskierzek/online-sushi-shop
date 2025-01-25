package com.sushiShop.onlineSushiShop.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SubCategory {
    NEW_ITEMS("New items");

    private final String value;

    SubCategory(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
