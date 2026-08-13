package com.dental.model;

public class Appointment {
    private String id;
    private String patientName;
    private String doctor;
    private String date;
    private String time;
    private String handledBy;

    public Appointment(String id, String patientName, String doctor, String date, String time, String handledBy) {
        this.id = id;
        this.patientName = patientName;
        this.doctor = doctor;
        this.date = date;
        this.time = time;
        this.handledBy = handledBy;
    }

    public String getId() {
        return id;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDoctor() {
        return doctor;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getHandledBy() {
        return handledBy;
    }
}