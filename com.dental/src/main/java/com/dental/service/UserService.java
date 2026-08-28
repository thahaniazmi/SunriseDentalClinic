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
        // only seed the defaults when the table is empty
        // so staff added through the Admin UI are kept between restarts
        if (dao.count() > 0) {
            return;
        }
        addUser(new User("S001", "Admin", "admin", "123", "Admin"));
        addUser(new User("S002", "User", "user", "123", "Staff"));
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