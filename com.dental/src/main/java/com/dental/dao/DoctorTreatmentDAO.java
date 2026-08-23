package com.dental.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.dental.database.DatabaseConnection;
import com.dental.model.TreatmentItem;

public class DoctorTreatmentDAO {

    public int count() {
        String sql = "SELECT COUNT(*) FROM doctor_treatments";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not check doctor treatments table", e);
        }
    }

    public List<TreatmentItem> findAllForDoctor(String doctorId) {
        String sql = "SELECT name, price FROM doctor_treatments WHERE doctor_id = ? ORDER BY name";
        List<TreatmentItem> treatments = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
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

    public void replaceForDoctor(String doctorId, List<TreatmentItem> treatments) {
        String deleteSql = "DELETE FROM doctor_treatments WHERE doctor_id = ?";
        String insertSql = "INSERT INTO doctor_treatments (doctor_id, name, price) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
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
}