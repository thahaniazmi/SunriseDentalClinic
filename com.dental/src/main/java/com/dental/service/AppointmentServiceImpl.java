package com.dental.service;

import java.time.LocalDate;
import java.util.List;

import com.dental.dao.AppointmentDAO;
import com.dental.model.Appointment;
import com.dental.model.TreatmentItem;

public class AppointmentServiceImpl implements AppointmentService {
    private AppointmentDAO dao = new AppointmentDAO();

    public AppointmentServiceImpl() {
        seedIfEmpty();
    }

    private void seedIfEmpty() {
        // sample appointments on different days so the lists are not empty
        if (dao.count() > 0) {
            return;
        }
        LocalDate today = LocalDate.now();
        String yesterday = today.minusDays(1).toString();
        String twoDaysAgo = today.minusDays(2).toString();
        String lastWeek = today.minusDays(7).toString();
        String tomorrow = today.plusDays(1).toString();
        String inTwoDays = today.plusDays(2).toString();
        String inThreeDays = today.plusDays(3).toString();
        String nextWeek = today.plusDays(7).toString();
        String threeWeeksAgo = today.minusDays(21).toString();
        String fiveWeeksAgo = today.minusDays(35).toString();
        String sixDaysAgo = today.minusDays(6).toString();
        String fourDaysAgo = today.minusDays(4).toString();
        String threeDaysAgo = today.minusDays(3).toString();
        String inFourDays = today.plusDays(4).toString();
        String inFiveDays = today.plusDays(5).toString();
        String inSixDays = today.plusDays(6).toString();
        String inTwoWeeks = today.plusDays(14).toString();

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

        // some older and further-out bookings
        addAppointment(new Appointment("A018", "Nuwan Rathnayake", "0771230001", "Dr. Gomes", 750.0,
                List.of(new TreatmentItem("Dental X-Ray", 2200.0)), fiveWeeksAgo, "09:00", "Sanduni Weerakkody", "S007"));
        addAppointment(new Appointment("A019", "Oshini Dissanayake", "0771230002", "Dr. Anna", 500.0,
                List.of(new TreatmentItem("Cleaning", 1500.0), new TreatmentItem("Scaling", 3500.0)), threeWeeksAgo, "10:30", "Nadeesha Silva", "S004"));
        addAppointment(new Appointment("A020", "Hasitha Gunawardena", "0771230003", "Dr. Malik", 700.0,
                List.of(new TreatmentItem("Root Canal", 13000.0)), threeWeeksAgo, "14:00", "Ruwan Fernando", "S005"));
        addAppointment(new Appointment("A021", "Sanduni Karunaratne", "0771230004", "Ms. Nisa", 450.0,
                List.of(new TreatmentItem("Whitening", 4200.0)), sixDaysAgo, "09:30", "Lahiru Silva", "S008"));
        addAppointment(new Appointment("A022", "Tharaka Peiris", "0771230005", "Dr. Fernando", 600.0,
                List.of(new TreatmentItem("Extraction", 3000.0)), fourDaysAgo, "11:00", "Nadeesha Silva", "S004"));
        addAppointment(new Appointment("A023", "Madhusha Wijesinghe", "0771230006", "Dr. Gomes", 750.0,
                List.of(new TreatmentItem("Cleaning", 1900.0), new TreatmentItem("Fluoride Treatment", 1100.0)), threeDaysAgo, "15:00", "Ruwan Fernando", "S005"));
        addAppointment(new Appointment("A024", "Dilshan Abeysinghe", "0771230007", "Dr. Anna", 500.0,
                List.of(new TreatmentItem("Filling", 3100.0)), inFourDays, "08:30", "Sanduni Weerakkody", "S007"));
        addAppointment(new Appointment("A025", "Nipuni Herath", "0771230008", "Dr. Perera", 800.0,
                List.of(new TreatmentItem("Braces", 48000.0)), inFiveDays, "10:00", "Lahiru Silva", "S008"));
        addAppointment(new Appointment("A026", "Charith Senanayake", "0771230009", "Dr. Malik", 700.0,
                List.of(new TreatmentItem("Cleaning", 1750.0), new TreatmentItem("Dental X-Ray", 2250.0)), inSixDays, "13:30", "Nadeesha Silva", "S004"));
        addAppointment(new Appointment("A027", "Yasas Fernando", "0771230010", "Dr. Gomes", 750.0,
                List.of(new TreatmentItem("Whitening", 4500.0)), inTwoWeeks, "14:30", "Ruwan Fernando", "S005"));
    }

    @Override
    public Appointment addAppointment(Appointment appointment) {
        if (appointment.getPatientName() == null || appointment.getPatientName().trim().isEmpty()) {
            throw new IllegalArgumentException("Patient name is required");
        }
        if (appointment.getPatientPhone() == null || !appointment.getPatientPhone().matches("\\d{10}")) {
            throw new IllegalArgumentException("Phone number must be exactly 10 digits");
        }
        return dao.add(appointment);
    }

    @Override
    public List<Appointment> viewAppointments() {
        return dao.findAll();
    }

    @Override
    public Appointment findById(String id) {
        return dao.findById(id);
    }

    @Override
    public List<Appointment> searchByPatientName(String patientName) {
        return dao.findByPatientName(patientName);
    }

    @Override
    public List<Appointment> searchByDate(String fromDate, String toDate) {
        return dao.findByDateRange(fromDate, toDate);
    }

    @Override
    public List<Appointment> searchUpcoming(String fromDate) {
        return dao.findUpcoming(fromDate);
    }

    @Override
    public List<Appointment> searchByStaffId(String staffId) {
        return dao.findByStaffId(staffId);
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
        dao.update(appointment);
    }
}