package com.dental.dao;

import java.util.List;

import com.dental.model.Appointment;

public interface AppointmentDAO {
    void save(Appointment appointment);

    List<Appointment> findAll();
}