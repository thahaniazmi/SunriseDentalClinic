package com.dental.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static DBConnection instance;
    private static final String URL = "jdbc:sqlite:sunrise_clinic.db";

    private DBConnection() {
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
            instance.createTables();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private void createTables() {
        String sql = ""
                + "CREATE TABLE IF NOT EXISTS users ("
                + "staff_id TEXT PRIMARY KEY, "
                + "name TEXT NOT NULL, "
                + "username TEXT NOT NULL UNIQUE, "
                + "password TEXT NOT NULL, "
                + "role TEXT NOT NULL);"

                + "CREATE TABLE IF NOT EXISTS doctors ("
                + "doctor_id TEXT PRIMARY KEY, "
                + "name TEXT NOT NULL, "
                + "consultation_fee REAL NOT NULL);"

                + "CREATE TABLE IF NOT EXISTS doctor_treatments ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "doctor_id TEXT NOT NULL, "
                + "name TEXT NOT NULL, "
                + "price REAL NOT NULL);"

                + "CREATE TABLE IF NOT EXISTS appointments ("
                + "appointment_id TEXT PRIMARY KEY, "
                + "patient_name TEXT NOT NULL, "
                + "patient_phone TEXT NOT NULL, "
                + "doctor TEXT NOT NULL, "
                + "consultation_fee REAL NOT NULL, "
                + "appointment_date TEXT NOT NULL, "
                + "appointment_time TEXT NOT NULL, "
                + "handled_by TEXT NOT NULL, "
                + "handled_by_id TEXT NOT NULL);"

                + "CREATE TABLE IF NOT EXISTS appointment_items ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "appointment_id TEXT NOT NULL, "
                + "name TEXT NOT NULL, "
                + "amount REAL NOT NULL);";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Could not create tables", e);
        }
    }
}
