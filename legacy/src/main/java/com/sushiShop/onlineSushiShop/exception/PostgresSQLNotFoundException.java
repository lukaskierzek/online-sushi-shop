package com.sushiShop.onlineSushiShop.exception;

public class PostgresSQLNotFoundException extends RuntimeException {
    public PostgresSQLNotFoundException(String message) {
        super(message);
    }
}
