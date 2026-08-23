package com.dental.service;

import java.util.List;

import com.dental.dao.DoctorTreatmentDAO;
import com.dental.model.Doctor;
import com.dental.model.TreatmentItem;

public class DoctorTreatmentService {
    private DoctorTreatmentDAO dao = new DoctorTreatmentDAO();

    public DoctorTreatmentService() {
        seedIfEmpty();
    }

    public List<TreatmentItem> getAllForDoctor(String doctorId) {
        return dao.findAllForDoctor(doctorId);
    }

    public void updateTreatments(String doctorId, List<TreatmentItem> treatments) {
        dao.replaceForDoctor(doctorId, treatments);
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
        if (dao.count() > 0) {
            return;
        }
        DoctorService doctorService = new DoctorService();
        for (Doctor doctor : doctorService.getAllDoctors()) {
            updateTreatments(doctor.getId(), seedListFor(doctor.getName()));
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