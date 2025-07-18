package com.sushiShop.onlineSushiShop.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Subcategory {
    NEW_ITEM("NEW-ITEM"),
    VEGE("VEGE"),
    BESTSELLER("*");

    private final String value;

    Subcategory(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
