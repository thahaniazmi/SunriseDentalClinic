package com.dental.service;

import com.dental.model.Appointment;
import com.dental.model.TreatmentItem;

public class BillingService {
    public String generateReceipt(Appointment appointment) {
        StringBuilder sb = new StringBuilder();
        sb.append("======== SUNRISE DENTAL CLINIC ========\n");
        sb.append("Receipt\n\n");
        sb.append("Appointment ID: ").append(appointment.getId()).append("\n");
        sb.append("Patient: ").append(appointment.getPatientName()).append("\n");
        sb.append("Phone: ").append(appointment.getPatientPhone()).append("\n");
        sb.append("Doctor: ").append(appointment.getDoctor()).append("\n");
        sb.append("Date: ").append(appointment.getDate()).append("\n");
        sb.append("Time: ").append(appointment.getTime()).append("\n\n");
        sb.append("Item                          Amount (LKR)\n");
        sb.append("-------------------------------------------\n");
        sb.append(String.format("%-28s %12.2f%n", "Consultation Fee", appointment.getConsultationFee()));
        for (TreatmentItem item : appointment.getItems()) {
            sb.append(String.format("%-28s %12.2f%n", item.getName(), item.getCost()));
        }
        sb.append("-------------------------------------------\n");
        sb.append(String.format("%-28s %12.2f%n", "TOTAL", appointment.getTotal()));
        sb.append("===========================================");
        return sb.toString();
    }
}
