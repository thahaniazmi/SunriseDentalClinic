package com.dental.service;

import com.dental.factory.ReportFactory;
import com.dental.model.Appointment;

public class BillingService {
    public String generateReceipt(Appointment appointment) {
        return ReportFactory.createReceipt(appointment).generate();
    }
}