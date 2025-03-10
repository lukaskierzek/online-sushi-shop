package com.sushiShop.onlineSushiShop.enums;

public enum Database {
    POSTGRES_ONLINESUSHISHOP_TEST("onlinesushishop_test"),
    POSTGRES_ONLINESUSHISHOP("onlinesushishop");

    private final String databaseName;

    Database(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseName() {
        return this.databaseName;
    }
}
