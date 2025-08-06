package com.romander.bookingapp.config;

import org.testcontainers.containers.MySQLContainer;

public class CustomMySqlContainer extends MySQLContainer<CustomMySqlContainer> {
    private static final String IMAGE_NAME = "mysql:8.0";

    private static CustomMySqlContainer instance;

    public CustomMySqlContainer() {
        super(IMAGE_NAME);
    }

    public static synchronized CustomMySqlContainer getInstance() {
        if (instance == null) {
            instance = new CustomMySqlContainer();
        }
        return instance;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("TESt_DB_URL", getJdbcUrl());
        System.setProperty("TESt_DB_USER", getUsername());
        System.setProperty("TESt_DB_PASSWORD", getPassword());
    }

    @Override
    public void stop() {
    }
}
