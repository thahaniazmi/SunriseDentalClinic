package com.dental.factory;

import com.dental.model.Appointment;

public class ReportFactory {

    // central place for creating the right report for a situation
    public static Report createReceipt(Appointment appointment) {
        return new ReceiptReport(appointment);
    }
}