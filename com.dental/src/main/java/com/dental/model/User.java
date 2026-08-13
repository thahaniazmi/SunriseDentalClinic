package com.dental.model;

public class User {
    private String staffId;
    private String name;
    private String username;
    private String password;
    private String role;

    public User(String staffId, String name, String username, String password, String role) {
        this.staffId = staffId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}