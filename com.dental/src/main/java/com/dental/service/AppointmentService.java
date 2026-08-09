package com.dental.service;

import java.util.List;

import com.dental.model.Appointment;

public interface AppointmentService {
    void addAppointment(Appointment appointment);

    List<Appointment> getAllAppointments();
}