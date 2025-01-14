package com.sushiShop.onlineSushiShop.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

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

//    @JsonCreator
//    public static IsHidden fromValue(int value) {
//        return Arrays.stream(values())
//                .filter(isHidden -> isHidden.value == value)
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("Invalid value for IsHidden: " + value));
//    }
}
