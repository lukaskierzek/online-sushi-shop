package com.sushiShop.onlineSushiShop.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum IsHidden {
    NO(0),
    YES(1);

    private final int value;

    IsHidden(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

}
