package com.dental.service;

public class AuthService {
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "1234";

    public boolean login(String username, String password) {
        return USERNAME.equals(username) && PASSWORD.equals(password);
    }
}