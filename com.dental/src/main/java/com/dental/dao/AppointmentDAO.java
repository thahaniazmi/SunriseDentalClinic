package com.dental.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.dental.model.Appointment;
import com.dental.model.TreatmentItem;
import com.dental.database.DatabaseConnection;

public class AppointmentDAO {

    public int count() {
        String sql = "SELECT COUNT(*) FROM appointments";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not count appointments", e);
        }
    }

    // one reusable count used by all the report numbers
    public int count(String fromDate, String toDate, String dentistName) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE appointment_date >= ? AND appointment_date <= ?";
        if (dentistName != null) {
            sql += " AND doctor = ?";
        }
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fromDate);
            stmt.setString(2, toDate);
            if (dentistName != null) {
                stmt.setString(3, dentistName);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not count appointments", e);
        }
    }

    public Appointment add(Appointment appointment) {
        String id = appointment.getId() == null || appointment.getId().isEmpty() ? nextId() : appointment.getId();
        String insertAppointment = "INSERT INTO appointments "
                + "(appointment_id, patient_name, patient_address, patient_phone, doctor, consultation_fee, appointment_date, appointment_time, handled_by, handled_by_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String insertItem = "INSERT INTO appointment_items (appointment_id, name, amount) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement appStmt = conn.prepareStatement(insertAppointment);
             PreparedStatement itemStmt = conn.prepareStatement(insertItem)) {

            appStmt.setString(1, id);
            appStmt.setString(2, appointment.getPatientName());
            appStmt.setString(3, appointment.getPatientAddress());
            appStmt.setString(4, appointment.getPatientPhone());
            appStmt.setString(5, appointment.getDoctor());
            appStmt.setDouble(6, appointment.getConsultationFee());
            appStmt.setString(7, appointment.getDate());
            appStmt.setString(8, appointment.getTime());
            appStmt.setString(9, appointment.getHandledBy());
            appStmt.setString(10, appointment.getHandledById());
            appStmt.executeUpdate();

            for (TreatmentItem item : appointment.getItems()) {
                itemStmt.setString(1, id);
                itemStmt.setString(2, item.getName());
                itemStmt.setDouble(3, item.getCost());
                itemStmt.addBatch();
            }
            itemStmt.executeBatch();

            return new Appointment(id, appointment.getPatientName(), appointment.getPatientAddress(),
                    appointment.getPatientPhone(), appointment.getDoctor(), appointment.getConsultationFee(),
                    appointment.getItems(), appointment.getDate(), appointment.getTime(),
                    appointment.getHandledBy(), appointment.getHandledById());
        } catch (SQLException e) {
            throw new RuntimeException("Could not add appointment", e);
        }
    }

    public String nextId() {
        String sql = "SELECT appointment_id FROM appointments ORDER BY appointment_id DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int num = 1;
            if (rs.next()) {
                num = Integer.parseInt(rs.getString("appointment_id").substring(1)) + 1;
            }
            return String.format("A%03d", num);
        } catch (SQLException e) {
            throw new RuntimeException("Could not generate appointment id", e);
        }
    }

    public List<Appointment> findAll() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY appointment_id";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                appointments.add(mapRow(conn, rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load appointments", e);
        }
        return appointments;
    }

    public Appointment findById(String id) {
        String sql = "SELECT * FROM appointments WHERE appointment_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(conn, rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Could not find appointment", e);
        }
    }

    public List<Appointment> findByPatientName(String patientName) {
        List<Appointment> results = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patient_name LIKE ? ORDER BY appointment_id";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + patientName + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(conn, rs));
                }
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Could not search appointments", e);
        }
    }

    public List<Appointment> findByDateRange(String fromDate, String toDate) {
        List<Appointment> results = new ArrayList<>();
        String sql;
        boolean hasFrom = fromDate != null && !fromDate.isEmpty();
        boolean hasTo = toDate != null && !toDate.isEmpty();

        if (hasFrom && hasTo) {
            sql = "SELECT * FROM appointments WHERE appointment_date BETWEEN ? AND ? ORDER BY appointment_date";
        } else if (hasFrom) {
            sql = "SELECT * FROM appointments WHERE appointment_date >= ? ORDER BY appointment_date";
        } else if (hasTo) {
            sql = "SELECT * FROM appointments WHERE appointment_date <= ? ORDER BY appointment_date";
        } else {
            sql = "SELECT * FROM appointments ORDER BY appointment_date";
        }

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int index = 1;
            if (hasFrom) {
                stmt.setString(index++, fromDate);
            }
            if (hasTo) {
                stmt.setString(index, toDate);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(conn, rs));
                }
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Could not search appointments", e);
        }
    }

    public List<Appointment> findUpcoming(String fromDate) {
        String sql = "SELECT * FROM appointments WHERE appointment_date > ? "
                + "ORDER BY appointment_date ASC, appointment_time ASC";
        List<Appointment> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fromDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(conn, rs));
                }
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Could not search upcoming appointments", e);
        }
    }

    public List<Appointment> findByStaffId(String staffId) {
        String sql = "SELECT * FROM appointments WHERE handled_by_id = ? "
                + "ORDER BY appointment_date ASC, appointment_time ASC";
        List<Appointment> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, staffId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(conn, rs));
                }
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Could not search appointments by staff", e);
        }
    }

    public void update(Appointment appointment) {
        String sql = "UPDATE appointments SET patient_name = ?, patient_address = ?, patient_phone = ?, doctor = ?, consultation_fee = ?, "
                + "appointment_date = ?, appointment_time = ?, handled_by = ?, handled_by_id = ? WHERE appointment_id = ?";
        String deleteItems = "DELETE FROM appointment_items WHERE appointment_id = ?";
        String insertItem = "INSERT INTO appointment_items (appointment_id, name, amount) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             PreparedStatement delStmt = conn.prepareStatement(deleteItems);
             PreparedStatement itemStmt = conn.prepareStatement(insertItem)) {

            stmt.setString(1, appointment.getPatientName());
            stmt.setString(2, appointment.getPatientAddress());
            stmt.setString(3, appointment.getPatientPhone());
            stmt.setString(4, appointment.getDoctor());
            stmt.setDouble(5, appointment.getConsultationFee());
            stmt.setString(6, appointment.getDate());
            stmt.setString(7, appointment.getTime());
            stmt.setString(8, appointment.getHandledBy());
            stmt.setString(9, appointment.getHandledById());
            stmt.setString(10, appointment.getId());
            stmt.executeUpdate();

            delStmt.setString(1, appointment.getId());
            delStmt.executeUpdate();

            for (TreatmentItem item : appointment.getItems()) {
                itemStmt.setString(1, appointment.getId());
                itemStmt.setString(2, item.getName());
                itemStmt.setDouble(3, item.getCost());
                itemStmt.addBatch();
            }
            itemStmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Could not update appointment", e);
        }
    }

    // older rows point at staff accounts that no longer exist,
    // so point them at the two accounts we always have
    public void remapOldStaffToCurrentAccounts() {
        String toAdmin = "UPDATE appointments SET handled_by_id = 'S001', handled_by = 'Admin' "
                + "WHERE handled_by_id IN ('S003', 'S005', 'S007')";
        String toUser = "UPDATE appointments SET handled_by_id = 'S002', handled_by = 'User' "
                + "WHERE handled_by_id IN ('S004', 'S006', 'S008')";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(toAdmin);
            stmt.executeUpdate(toUser);
        } catch (SQLException e) {
            throw new RuntimeException("Could not update staff on appointments", e);
        }
    }

    private List<TreatmentItem> loadItems(Connection conn, String appointmentId) throws SQLException {
        List<TreatmentItem> items = new ArrayList<>();
        String sql = "SELECT name, amount FROM appointment_items WHERE appointment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, appointmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new TreatmentItem(rs.getString("name"), rs.getDouble("amount")));
                }
            }
        }
        return items;
    }

    private Appointment mapRow(Connection conn, ResultSet rs) throws SQLException {
        String id = rs.getString("appointment_id");
        return new Appointment(
                id,
                rs.getString("patient_name"),
                rs.getString("patient_address"),
                rs.getString("patient_phone"),
                rs.getString("doctor"),
                rs.getDouble("consultation_fee"),
                loadItems(conn, id),
                rs.getString("appointment_date"),
                rs.getString("appointment_time"),
                rs.getString("handled_by"),
                rs.getString("handled_by_id"));
    }
}