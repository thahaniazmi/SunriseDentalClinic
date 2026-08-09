package com.dental.service;

import java.util.List;

import com.dental.dao.AppointmentDAO;
import com.dental.dao.AppointmentDAOImpl;
import com.dental.model.Appointment;

public class AppointmentServiceImpl implements AppointmentService {
    private AppointmentDAO appointmentDAO = new AppointmentDAOImpl();

    @Override
    public void addAppointment(Appointment appointment) {
        appointmentDAO.save(appointment);
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentDAO.findAll();
    }
}