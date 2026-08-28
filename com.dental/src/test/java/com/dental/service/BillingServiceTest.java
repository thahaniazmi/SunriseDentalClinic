package com.dental.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.dental.model.Appointment;
import com.dental.model.TreatmentItem;

public class BillingServiceTest {

    @Test
    public void billShowsLineItemsAndGrandTotal() {
        Appointment appointment = new Appointment("A200", "Bill Patient", "No. 89, Duplication Road, Colombo 03",
                "0779998887", "Dr. Malik", 700.0,
                List.of(new TreatmentItem("Root Canal", 14000.0)),
                "2026-08-23", "11:30", "Nadeesha Silva", "S004");

        String bill = new BillingService().generateReceipt(appointment);

        assertTrue(bill.contains("Consultation Fee"));
        assertTrue(bill.contains("Root Canal"));
        assertTrue(bill.contains(String.format("%12.2f", 14700.0).trim()));
    }

    @Test
    public void totalMatchesModelCalculation() {
        Appointment appointment = new Appointment("A201", "Sum Patient", "No. 23, High Level Road, Maharagama",
                "0711111111", "Dr. Anna", 500.0,
                List.of(new TreatmentItem("Cleaning", 1500.5)),
                "2026-08-23", "15:00", "Kasun Perera", "S003");
        assertEquals(2000.5, appointment.getTotal());
        assertTrue(new BillingService().generateReceipt(appointment).contains("2000.50"));
    }
}
