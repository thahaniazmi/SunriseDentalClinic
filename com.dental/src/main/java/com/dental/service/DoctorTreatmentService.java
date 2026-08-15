package com.dental.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.dental.model.Doctor;
import com.dental.model.TreatmentItem;
import com.dental.util.DBConnection;

public class DoctorTreatmentService {
    public DoctorTreatmentService() {
        seedIfEmpty();
    }

    public List<TreatmentItem> getAllForDoctor(String doctorId) {
        String sql = "SELECT name, price FROM doctor_treatments WHERE doctor_id = ? ORDER BY name";
        List<TreatmentItem> treatments = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, doctorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    treatments.add(new TreatmentItem(rs.getString("name"), rs.getDouble("price")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load treatments for doctor", e);
        }
        return treatments;
    }

    public void updateTreatments(String doctorId, List<TreatmentItem> treatments) {
        String deleteSql = "DELETE FROM doctor_treatments WHERE doctor_id = ?";
        String insertSql = "INSERT INTO doctor_treatments (doctor_id, name, price) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            deleteStmt.setString(1, doctorId);
            deleteStmt.executeUpdate();
            for (TreatmentItem treatment : treatments) {
                insertStmt.setString(1, doctorId);
                insertStmt.setString(2, treatment.getName());
                insertStmt.setDouble(3, treatment.getCost());
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Could not save treatments for doctor", e);
        }
    }

    public void populateDefaults(String doctorId) {
        updateTreatments(doctorId, defaultTreatments());
    }

    public static List<TreatmentItem> defaultTreatments() {
        return List.of(
                new TreatmentItem("Cleaning", 1500.0),
                new TreatmentItem("Filling", 3000.0),
                new TreatmentItem("Extraction", 2500.0),
                new TreatmentItem("Root Canal", 12000.0),
                new TreatmentItem("Whitening", 4000.0),
                new TreatmentItem("Braces", 45000.0),
                new TreatmentItem("Fluoride Treatment", 1000.0),
                new TreatmentItem("Scaling", 3500.0),
                new TreatmentItem("Dental X-Ray", 2000.0));
    }

    private void seedIfEmpty() {
        boolean empty = false;
        String countSql = "SELECT COUNT(*) FROM doctor_treatments";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next()) {
                empty = rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not check doctor treatments table", e);
        }
        if (empty) {
            DoctorService doctorService = new DoctorService();
            for (Doctor doctor : doctorService.getAllDoctors()) {
                updateTreatments(doctor.getId(), seedListFor(doctor.getName()));
            }
        }
    }

    private List<TreatmentItem> seedListFor(String doctorName) {
        switch (doctorName) {
            case "Dr. Anna":
                return List.of(
                        new TreatmentItem("Cleaning", 1500.0),
                        new TreatmentItem("Filling", 3000.0),
                        new TreatmentItem("Fluoride Treatment", 1000.0),
                        new TreatmentItem("Scaling", 3500.0),
                        new TreatmentItem("Whitening", 4500.0));
            case "Dr. Malik":
                return List.of(
                        new TreatmentItem("Cleaning", 1750.0),
                        new TreatmentItem("Filling", 3250.0),
                        new TreatmentItem("Root Canal", 12500.0),
                        new TreatmentItem("Extraction", 2750.0),
                        new TreatmentItem("Dental X-Ray", 2250.0));
            case "Ms. Nisa":
                return List.of(
                        new TreatmentItem("Cleaning", 1400.0),
                        new TreatmentItem("Whitening", 4200.0),
                        new TreatmentItem("Fluoride Treatment", 1100.0),
                        new TreatmentItem("Scaling", 3400.0));
            case "Dr. Perera":
                return List.of(
                        new TreatmentItem("Root Canal", 14000.0),
                        new TreatmentItem("Braces", 50000.0),
                        new TreatmentItem("Dental X-Ray", 2500.0),
                        new TreatmentItem("Extraction", 3300.0),
                        new TreatmentItem("Cleaning", 2100.0));
            case "Dr. Fernando":
                return List.of(
                        new TreatmentItem("Cleaning", 1600.0),
                        new TreatmentItem("Extraction", 2900.0),
                        new TreatmentItem("Whitening", 4300.0),
                        new TreatmentItem("Braces", 46000.0),
                        new TreatmentItem("Filling", 3400.0));
            default:
                return defaultTreatments();
        }
    }
}