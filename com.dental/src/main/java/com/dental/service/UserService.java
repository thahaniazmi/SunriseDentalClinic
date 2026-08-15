package com.dental.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.dental.model.User;
import com.dental.util.DBConnection;

public class UserService {
    public UserService() {
        seedIfEmpty();
    }

    private void seedIfEmpty() {
        boolean empty = false;
        String countSql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next()) {
                empty = rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not check users table", e);
        }
        if (empty) {
            addUser(new User("S001", "Admin", "admin@sunshine.lk", "1234", "Admin"));
            addUser(new User("S002", "User", "user@sunshine.lk", "1234", "Staff"));
            addUser(new User("S003", "Kasun Perera", "kasun@sunshine.lk", "1234", "Staff"));
            addUser(new User("S004", "Nadeesha Silva", "nadeesha@sunshine.lk", "1234", "Staff"));
            addUser(new User("S005", "Ruwan Fernando", "ruwan@sunshine.lk", "1234", "Staff"));
            addUser(new User("S006", "Dilani Jayasinghe", "dilani@sunshine.lk", "1234", "Staff"));
        }
    }

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Could not find user", e);
        }
    }

    public void addUser(User user) {
        String sql = "INSERT INTO users (staff_id, name, username, password, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getStaffId());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getUsername());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getRole());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not add user", e);
        }
    }

    public void editUser(User updated) {
        String sql = "UPDATE users SET name = ?, username = ?, password = ?, role = ? WHERE staff_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, updated.getName());
            stmt.setString(2, updated.getUsername());
            stmt.setString(3, updated.getPassword());
            stmt.setString(4, updated.getRole());
            stmt.setString(5, updated.getStaffId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not edit user", e);
        }
    }

    public void removeUser(String staffId) {
        String sql = "DELETE FROM users WHERE staff_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, staffId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not remove user", e);
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load users", e);
        }
        return users;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getString("staff_id"),
                rs.getString("name"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role"));
    }
}
