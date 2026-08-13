package com.dental.service;

import java.util.ArrayList;
import java.util.List;

import com.dental.model.User;

public class UserService {
    private List<User> users = new ArrayList<>();

    public UserService() {
        users.add(new User("S001", "Dr. Anna", "staff", "1234", "Staff"));
        users.add(new User("S002", "Dr. Malik", "admin", "1234", "Admin"));
        users.add(new User("S003", "Ms. Nisa", "nisa", "1234", "Staff"));
    }

    public User login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void editUser(User updated) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getStaffId().equals(updated.getStaffId())) {
                users.set(i, updated);
                return;
            }
        }
    }

    public void removeUser(String staffId) {
        users.removeIf(user -> user.getStaffId().equals(staffId));
    }

    public List<User> getAllUsers() {
        return users;
    }
}