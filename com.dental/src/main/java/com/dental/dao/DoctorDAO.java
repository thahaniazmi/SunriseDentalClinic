package com.dental.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.dental.database.DatabaseConnection;
import com.dental.model.Doctor;

public class DoctorDAO {

    public int count() {
        String sql = "SELECT COUNT(*) FROM doctors";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not check doctors table", e);
        }
    }

    public List<Doctor> findAll() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors ORDER BY doctor_id";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
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
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
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

    public String add(String id, String name, double consultationFee) {
        if (id == null) {
            id = nextId();
        }
        String sql = "INSERT INTO doctors (doctor_id, name, consultation_fee) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, name);
            stmt.setDouble(3, consultationFee);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not add doctor", e);
        }
        return id;
    }

    public void updateFee(String id, double consultationFee) {
        String sql = "UPDATE doctors SET consultation_fee = ? WHERE doctor_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, consultationFee);
            stmt.setString(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not edit consultation fee", e);
        }
    }

    public String nextId() {
        String sql = "SELECT doctor_id FROM doctors ORDER BY doctor_id DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
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