package com.dental.service;

import java.util.List;

import com.dental.model.Appointment;

public interface AppointmentService {
    Appointment addAppointment(Appointment appointment);

    List<Appointment> viewAppointments();

    Appointment findById(String id);

    List<Appointment> searchByPatientName(String patientName);

    List<Appointment> searchByDate(String fromDate, String toDate);

    List<Appointment> searchUpcoming(String fromDate);

    List<Appointment> searchByStaffId(String staffId);

    List<Appointment> search(String query);

    void updateAppointment(Appointment appointment);

    int countAppointments(String fromDate, String toDate, String dentistName);
}
