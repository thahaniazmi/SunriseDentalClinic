package com.dental.factory;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.dental.model.Appointment;
import com.dental.model.TreatmentItem;

public class ReceiptReportTest {

    private Appointment sampleAppointment() {
        return new Appointment("A100", "Test Patient", "0771234567", "Dr. Anna", 500.0,
                List.of(new TreatmentItem("Cleaning", 1500.0), new TreatmentItem("Filling", 3250.0)),
                "2026-08-23", "09:00", "Kasun Perera", "S003");
    }

    @Test
    public void receiptContainsAppointmentDetails() {
        String receipt = new ReceiptReport(sampleAppointment()).generate();
        assertTrue(receipt.contains("A100"));
        assertTrue(receipt.contains("Test Patient"));
        assertTrue(receipt.contains("Dr. Anna"));
        assertTrue(receipt.contains("2026-08-23"));
        assertTrue(receipt.contains("09:00"));
    }

    @Test
    public void receiptListsEveryTreatment() {
        String receipt = new ReceiptReport(sampleAppointment()).generate();
        assertTrue(receipt.contains("Cleaning"));
        assertTrue(receipt.contains("Filling"));
    }

    @Test
    public void totalIsConsultationFeePlusTreatments() {
        String receipt = new ReceiptReport(sampleAppointment()).generate();
        assertTrue(receipt.contains("TOTAL"));
        assertTrue(receipt.contains(String.format("%12.2f", 5250.0).trim()));
    }

    @Test
    public void boundaryNoTreatmentsStillPrintsTotal() {
        Appointment empty = new Appointment("A101", "Empty Patient", "0770000000", "Dr. Anna", 500.0,
                List.of(), "2026-08-23", "10:00", "Kasun Perera", "S003");
        String receipt = new ReceiptReport(empty).generate();
        assertTrue(receipt.contains("TOTAL"));
        assertTrue(receipt.contains(String.format("%12.2f", 500.0).trim()));
    }
}
