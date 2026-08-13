package com.dental.service;

import java.util.ArrayList;
import java.util.List;

import com.dental.model.Appointment;

public class AppointmentServiceImpl implements AppointmentService {
    private List<Appointment> appointments = new ArrayList<>();

    @Override
    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    @Override
    public List<Appointment> viewAppointments() {
        return appointments;
    }
}