package com.dental.service;

import java.util.List;

import com.dental.dao.UserDAO;
import com.dental.model.User;

public class UserService {
    private UserDAO dao = new UserDAO();

    public UserService() {
        seedIfEmpty();
    }

    private void seedIfEmpty() {
        // some accounts so the app is not empty on first run
        if (dao.count() > 0) {
            return;
        }
        addUser(new User("S001", "Admin", "admin@sunshine.lk", "1234", "Admin"));
        addUser(new User("S002", "User", "user@sunshine.lk", "1234", "Staff"));
        addUser(new User("S003", "Kasun Perera", "kasun@sunshine.lk", "1234", "Staff"));
        addUser(new User("S004", "Nadeesha Silva", "nadeesha@sunshine.lk", "1234", "Staff"));
        addUser(new User("S005", "Ruwan Fernando", "ruwan@sunshine.lk", "1234", "Staff"));
        addUser(new User("S006", "Dilani Jayasinghe", "dilani@sunshine.lk", "1234", "Staff"));
        addUser(new User("S007", "Sanduni Weerakkody", "sanduni@sunshine.lk", "1234", "Staff"));
        addUser(new User("S008", "Lahiru Silva", "lahiru@sunshine.lk", "1234", "Staff"));
    }

    public User findByUsername(String username) {
        return dao.findByUsername(username);
    }

    public void addUser(User user) {
        dao.add(user);
    }

    public void editUser(User updated) {
        dao.update(updated);
    }

    public void removeUser(String staffId) {
        dao.remove(staffId);
    }

    public List<User> getAllUsers() {
        return dao.findAll();
    }
}