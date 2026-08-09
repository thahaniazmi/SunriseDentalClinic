package com.dental.model;

public class Appointment {
    private String id;
    private String patientName;
    private String doctor;
    private String date;
    private String time;

    public Appointment(String id, String patientName, String doctor, String date, String time) {
        this.id = id;
        this.patientName = patientName;
        this.doctor = doctor;
        this.date = date;
        this.time = time;
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

    public void display() {
        System.out.println("ID: " + id + " | Patient: " + patientName
                + " | Doctor: " + doctor + " | Date: " + date + " | Time: " + time);
    }
}