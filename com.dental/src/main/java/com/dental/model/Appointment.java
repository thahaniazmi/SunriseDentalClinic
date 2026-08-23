package com.dental.model;

import java.util.List;

public class Appointment {
    private String id;
    private String patientName;
    private String patientPhone;
    private String doctor;
    private double consultationFee;
    private List<TreatmentItem> items;
    private String date;
    private String time;
    private String handledBy;
    private String handledById;

    public Appointment() {
    }

    public Appointment(String id, String patientName, String patientPhone, String doctor, double consultationFee,
                       List<TreatmentItem> items, String date, String time, String handledBy, String handledById) {
        this.id = id;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.doctor = doctor;
        this.consultationFee = consultationFee;
        this.items = items;
        this.date = date;
        this.time = time;
        this.handledBy = handledBy;
        this.handledById = handledById;
    }

    public String getId() {
        return id;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public String getDoctor() {
        return doctor;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public List<TreatmentItem> getItems() {
        return items;
    }

    public double getTotal() {
        double total = consultationFee;
        for (TreatmentItem item : items) {
            total += item.getCost();
        }
        return total;
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

    public String getHandledById() {
        return handledById;
    }
}
