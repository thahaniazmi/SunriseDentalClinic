package com.dental.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.dental.model.Appointment;
import com.dental.model.TreatmentItem;
import com.dental.util.DBConnection;

public class AppointmentServiceImpl implements AppointmentService {
    public AppointmentServiceImpl() {
        seedIfEmpty();
    }

    private void seedIfEmpty() {
        boolean empty = false;
        String countSql = "SELECT COUNT(*) FROM appointments";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next()) {
                empty = rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not check appointments table", e);
        }
        if (empty) {
            LocalDate today = LocalDate.now();
            String yesterday = today.minusDays(1).toString();
            String twoDaysAgo = today.minusDays(2).toString();
            String lastWeek = today.minusDays(7).toString();
            String tomorrow = today.plusDays(1).toString();
            String inTwoDays = today.plusDays(2).toString();
            String inThreeDays = today.plusDays(3).toString();
            String nextWeek = today.plusDays(7).toString();

            Appointment a1 = new Appointment("A001", "Kavindu Perera", "0712345678", "Dr. Anna", 500.0,
                    List.of(new TreatmentItem("Cleaning", 1500.0)),
                    lastWeek, "09:00", "Kasun Perera", "S003");
            Appointment a2 = new Appointment("A002", "Nimal Fernando", "0723456789", "Dr. Malik", 700.0,
                    List.of(new TreatmentItem("Filling", 3250.0)),
                    lastWeek, "10:30", "Nadeesha Silva", "S004");
            Appointment a3 = new Appointment("A003", "Sachini Silva", "0734567890", "Ms. Nisa", 450.0,
                    List.of(new TreatmentItem("Whitening", 4200.0),
                            new TreatmentItem("Fluoride Treatment", 1100.0)),
                    twoDaysAgo, "11:00", "Kasun Perera", "S003");
            Appointment a4 = new Appointment("A004", "Tharindu Weerasinghe", "0745678901", "Dr. Perera", 800.0,
                    List.of(new TreatmentItem("Root Canal", 14000.0)),
                    yesterday, "09:30", "Ruwan Fernando", "S005");
            Appointment a5 = new Appointment("A005", "Amaya Jayasuriya", "0756789012", "Dr. Fernando", 600.0,
                    List.of(new TreatmentItem("Extraction", 2900.0),
                            new TreatmentItem("Cleaning", 1600.0)),
                    yesterday, "14:00", "Dilani Jayasinghe", "S006");
            Appointment a6 = new Appointment("A006", "Isuru Bandara", "0767890123", "Dr. Anna", 500.0,
                    List.of(new TreatmentItem("Scaling", 3500.0)),
                    today.toString(), "09:00", "Kasun Perera", "S003");
            Appointment a7 = new Appointment("A007", "Kasun Wickramasinghe", "0778901234", "Dr. Malik", 700.0,
                    List.of(new TreatmentItem("Cleaning", 1750.0),
                            new TreatmentItem("Dental X-Ray", 2250.0)),
                    today.toString(), "10:00", "Nadeesha Silva", "S004");
            Appointment a8 = new Appointment("A008", "Nadeesha Gunasekara", "0789012345", "Ms. Nisa", 450.0,
                    List.of(new TreatmentItem("Cleaning", 1400.0),
                            new TreatmentItem("Fluoride Treatment", 1100.0)),
                    today.toString(), "11:30", "Ruwan Fernando", "S005");
            Appointment a9 = new Appointment("A009", "Ruwan Dias", "0790123456", "Dr. Perera", 800.0,
                    List.of(new TreatmentItem("Dental X-Ray", 2500.0),
                            new TreatmentItem("Extraction", 3300.0)),
                    today.toString(), "14:00", "Dilani Jayasinghe", "S006");
            Appointment a10 = new Appointment("A010", "Dilani Perera", "0701234567", "Dr. Fernando", 600.0,
                    List.of(new TreatmentItem("Whitening", 4300.0),
                            new TreatmentItem("Filling", 3400.0)),
                    today.toString(), "15:30", "Kasun Perera", "S003");
            Appointment a11 = new Appointment("A011", "Sahan Jayawardena", "0719876543", "Dr. Anna", 500.0,
                    List.of(new TreatmentItem("Filling", 3000.0),
                            new TreatmentItem("Fluoride Treatment", 1000.0)),
                    tomorrow, "08:30", "Nadeesha Silva", "S004");
            Appointment a12 = new Appointment("A012", "Thilini Rathnayake", "0775551234", "Dr. Malik", 700.0,
                    List.of(new TreatmentItem("Root Canal", 12500.0)),
                    tomorrow, "10:00", "Ruwan Fernando", "S005");
            Appointment a13 = new Appointment("A013", "Chamari Atapattu", "0764447890", "Ms. Nisa", 450.0,
                    List.of(new TreatmentItem("Whitening", 4200.0)),
                    tomorrow, "13:30", "Dilani Jayasinghe", "S006");
            Appointment a14 = new Appointment("A014", "Dasun Liyanage", "0753332109", "Dr. Fernando", 600.0,
                    List.of(new TreatmentItem("Braces", 46000.0)),
                    inTwoDays, "09:00", "Kasun Perera", "S003");
            Appointment a15 = new Appointment("A015", "Ishara Madushani", "0742228765", "Dr. Perera", 800.0,
                    List.of(new TreatmentItem("Cleaning", 2100.0),
                            new TreatmentItem("Dental X-Ray", 2500.0)),
                    inTwoDays, "11:00", "Nadeesha Silva", "S004");
            Appointment a16 = new Appointment("A016", "Pasindu Jayawardena", "0781113456", "Dr. Anna", 500.0,
                    List.of(new TreatmentItem("Whitening", 4500.0)),
                    inThreeDays, "10:30", "Ruwan Fernando", "S005");
            Appointment a17 = new Appointment("A017", "Kaushalya Fernando", "0712345679", "Dr. Malik", 700.0,
                    List.of(new TreatmentItem("Extraction", 2750.0),
                            new TreatmentItem("Filling", 3250.0)),
                    nextWeek, "14:30", "Dilani Jayasinghe", "S006");

            addAppointment(a1);
            addAppointment(a2);
            addAppointment(a3);
            addAppointment(a4);
            addAppointment(a5);
            addAppointment(a6);
            addAppointment(a7);
            addAppointment(a8);
            addAppointment(a9);
            addAppointment(a10);
            addAppointment(a11);
            addAppointment(a12);
            addAppointment(a13);
            addAppointment(a14);
            addAppointment(a15);
            addAppointment(a16);
            addAppointment(a17);
        }
    }

    @Override
    public Appointment addAppointment(Appointment appointment) {
        if (appointment.getPatientName() == null || appointment.getPatientName().trim().isEmpty()) {
            throw new IllegalArgumentException("Patient name is required");
        }
        String id = appointment.getId() == null || appointment.getId().isEmpty() ? nextId() : appointment.getId();
        String insertAppointment = "INSERT INTO appointments "
                + "(appointment_id, patient_name, patient_phone, doctor, consultation_fee, appointment_date, appointment_time, handled_by, handled_by_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String insertItem = "INSERT INTO appointment_items (appointment_id, name, amount) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement appStmt = conn.prepareStatement(insertAppointment);
             PreparedStatement itemStmt = conn.prepareStatement(insertItem)) {

            appStmt.setString(1, id);
            appStmt.setString(2, appointment.getPatientName());
            appStmt.setString(3, appointment.getPatientPhone());
            appStmt.setString(4, appointment.getDoctor());
            appStmt.setDouble(5, appointment.getConsultationFee());
            appStmt.setString(6, appointment.getDate());
            appStmt.setString(7, appointment.getTime());
            appStmt.setString(8, appointment.getHandledBy());
            appStmt.setString(9, appointment.getHandledById());
            appStmt.executeUpdate();

            for (TreatmentItem item : appointment.getItems()) {
                itemStmt.setString(1, id);
                itemStmt.setString(2, item.getName());
                itemStmt.setDouble(3, item.getCost());
                itemStmt.addBatch();
            }
            itemStmt.executeBatch();

            return new Appointment(id, appointment.getPatientName(), appointment.getPatientPhone(),
                    appointment.getDoctor(), appointment.getConsultationFee(), appointment.getItems(),
                    appointment.getDate(), appointment.getTime(), appointment.getHandledBy(), appointment.getHandledById());
        } catch (SQLException e) {
            throw new RuntimeException("Could not add appointment", e);
        }
    }

    private String nextId() {
        String sql = "SELECT appointment_id FROM appointments ORDER BY appointment_id DESC LIMIT 1";
        try (Connection conn = DBConnection.getInstance().getConnection();
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

    @Override
    public List<Appointment> viewAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY appointment_id";
        try (Connection conn = DBConnection.getInstance().getConnection();
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

    @Override
    public Appointment findById(String id) {
        String sql = "SELECT * FROM appointments WHERE appointment_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
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

    @Override
    public List<Appointment> searchByPatientName(String patientName) {
        List<Appointment> results = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patient_name LIKE ? ORDER BY appointment_id";
        try (Connection conn = DBConnection.getInstance().getConnection();
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

    @Override
    public List<Appointment> searchByDate(String fromDate, String toDate) {
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

        try (Connection conn = DBConnection.getInstance().getConnection();
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

    @Override
    public List<Appointment> searchUpcoming(String fromDate) {
        String sql = "SELECT * FROM appointments WHERE appointment_date > ? "
                + "ORDER BY appointment_date ASC, appointment_time ASC";
        List<Appointment> results = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
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

    @Override
    public List<Appointment> searchByStaffId(String staffId) {
        String sql = "SELECT * FROM appointments WHERE handled_by_id = ? "
                + "ORDER BY appointment_date ASC, appointment_time ASC";
        List<Appointment> results = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
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

    @Override
    public List<Appointment> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return viewAppointments();
        }
        String q = query.trim();
        Appointment byId = findById(q);
        if (byId != null) {
            return List.of(byId);
        }
        if (q.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return searchByDate(q, q);
        }
        return searchByPatientName(q);
    }

    @Override
    public void updateAppointment(Appointment appointment) {
        String sql = "UPDATE appointments SET patient_name = ?, patient_phone = ?, doctor = ?, consultation_fee = ?, "
                + "appointment_date = ?, appointment_time = ?, handled_by = ?, handled_by_id = ? WHERE appointment_id = ?";
        String deleteItems = "DELETE FROM appointment_items WHERE appointment_id = ?";
        String insertItem = "INSERT INTO appointment_items (appointment_id, name, amount) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             PreparedStatement delStmt = conn.prepareStatement(deleteItems);
             PreparedStatement itemStmt = conn.prepareStatement(insertItem)) {

            stmt.setString(1, appointment.getPatientName());
            stmt.setString(2, appointment.getPatientPhone());
            stmt.setString(3, appointment.getDoctor());
            stmt.setDouble(4, appointment.getConsultationFee());
            stmt.setString(5, appointment.getDate());
            stmt.setString(6, appointment.getTime());
            stmt.setString(7, appointment.getHandledBy());
            stmt.setString(8, appointment.getHandledById());
            stmt.setString(9, appointment.getId());
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

    @Override
    public void deleteAppointment(String id) {
        String deleteItems = "DELETE FROM appointment_items WHERE appointment_id = ?";
        String sql = "DELETE FROM appointments WHERE appointment_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement itemStmt = conn.prepareStatement(deleteItems);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            itemStmt.setString(1, id);
            itemStmt.executeUpdate();
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not cancel appointment", e);
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
