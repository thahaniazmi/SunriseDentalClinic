package com.dental.model;

public class Doctor {
    private String id;
    private String name;
    private double consultationFee;

    public Doctor() {
    }

    public Doctor(String id, String name, double consultationFee) {
        this.id = id;
        this.name = name;
        this.consultationFee = consultationFee;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    @Override
    public String toString() {
        return name + " (Rs " + consultationFee + ")";
    }
}
