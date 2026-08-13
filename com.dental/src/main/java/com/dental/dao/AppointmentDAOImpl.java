package com.dental.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.dental.model.Appointment;
import com.dental.util.DBConnection;

public class AppointmentDAOImpl implements AppointmentDAO {
    private static final String INSERT_SQL = "INSERT INTO appointments (id, patient_name, doctor, appointment_date, appointment_time) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_SQL = "SELECT * FROM appointments";

    @Override
    public void save(Appointment appointment) {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
            stmt.setString(1, appointment.getId());
            stmt.setString(2, appointment.getPatientName());
            stmt.setString(3, appointment.getDoctor());
            stmt.setString(4, appointment.getDate());
            stmt.setString(5, appointment.getTime());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not save appointment", e);
        }
    }

    @Override
    public List<Appointment> findAll() {
        List<Appointment> appointments = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {
            while (rs.next()) {
                appointments.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load appointments", e);
        }
        return appointments;
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        return new Appointment(
                rs.getString("id"),
                rs.getString("patient_name"),
                rs.getString("doctor"),
                rs.getString("appointment_date"),
                rs.getString("appointment_time"),
                ""
        );
    }
}