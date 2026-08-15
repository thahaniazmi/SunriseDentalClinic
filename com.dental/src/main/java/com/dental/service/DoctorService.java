package com.dental.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.dental.model.Doctor;
import com.dental.util.DBConnection;

public class DoctorService {
    public DoctorService() {
        seedIfEmpty();
    }

    private void seedIfEmpty() {
        boolean empty = false;
        String countSql = "SELECT COUNT(*) FROM doctors";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next()) {
                empty = rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not check doctors table", e);
        }
        if (empty) {
            String sql = "INSERT INTO doctors (doctor_id, name, consultation_fee) VALUES (?, ?, ?)";
            try (Connection conn = DBConnection.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "D001");
                stmt.setString(2, "Dr. Anna");
                stmt.setDouble(3, 500.0);
                stmt.executeUpdate();
                stmt.setString(1, "D002");
                stmt.setString(2, "Dr. Malik");
                stmt.setDouble(3, 700.0);
                stmt.executeUpdate();
                stmt.setString(1, "D003");
                stmt.setString(2, "Ms. Nisa");
                stmt.setDouble(3, 450.0);
                stmt.executeUpdate();
                stmt.setString(1, "D004");
                stmt.setString(2, "Dr. Perera");
                stmt.setDouble(3, 800.0);
                stmt.executeUpdate();
                stmt.setString(1, "D005");
                stmt.setString(2, "Dr. Fernando");
                stmt.setDouble(3, 600.0);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Could not seed doctors", e);
            }
        }
    }

    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors ORDER BY doctor_id";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                doctors.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load doctors", e);
        }
        return doctors;
    }

    public Doctor findById(String id) {
        String sql = "SELECT * FROM doctors WHERE doctor_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Could not find doctor", e);
        }
    }

    public String addDoctor(String name, double consultationFee) {
        String id = nextId();
        String sql = "INSERT INTO doctors (doctor_id, name, consultation_fee) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, name);
            stmt.setDouble(3, consultationFee);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not add doctor", e);
        }
        new DoctorTreatmentService().populateDefaults(id);
        return id;
    }

    public void editConsultationFee(String id, double consultationFee) {
        String sql = "UPDATE doctors SET consultation_fee = ? WHERE doctor_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, consultationFee);
            stmt.setString(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not edit consultation fee", e);
        }
    }

    private String nextId() {
        String sql = "SELECT doctor_id FROM doctors ORDER BY doctor_id DESC LIMIT 1";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int num = 1;
            if (rs.next()) {
                num = Integer.parseInt(rs.getString("doctor_id").substring(1)) + 1;
            }
            return String.format("D%03d", num);
        } catch (SQLException e) {
            throw new RuntimeException("Could not generate doctor id", e);
        }
    }

    private Doctor mapRow(ResultSet rs) throws SQLException {
        return new Doctor(
                rs.getString("doctor_id"),
                rs.getString("name"),
                rs.getDouble("consultation_fee"));
    }
}
