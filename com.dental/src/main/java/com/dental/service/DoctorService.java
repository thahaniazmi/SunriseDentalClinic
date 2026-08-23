package com.dental.service;

import java.util.List;

import com.dental.dao.DoctorDAO;
import com.dental.model.Doctor;

public class DoctorService {
    private DoctorDAO dao = new DoctorDAO();

    public DoctorService() {
        seedIfEmpty();
    }

    private void seedIfEmpty() {
        // same idea for the doctors
        if (dao.count() > 0) {
            return;
        }
        dao.add("D001", "Dr. Anna", 500.0);
        dao.add("D002", "Dr. Malik", 700.0);
        dao.add("D003", "Ms. Nisa", 450.0);
        dao.add("D004", "Dr. Perera", 800.0);
        dao.add("D005", "Dr. Fernando", 600.0);
        dao.add("D006", "Dr. Gomes", 750.0);
    }

    public List<Doctor> getAllDoctors() {
        return dao.findAll();
    }

    public Doctor findById(String id) {
        return dao.findById(id);
    }

    public String addDoctor(String name, double consultationFee) {
        String id = dao.add(null, name, consultationFee);
        new DoctorTreatmentService().populateDefaults(id);
        return id;
    }

    public void editConsultationFee(String id, double consultationFee) {
        dao.updateFee(id, consultationFee);
    }
}