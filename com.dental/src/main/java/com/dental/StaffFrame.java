package com.dental;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.dental.model.Appointment;
import com.dental.model.User;
import com.dental.service.AppointmentService;
import com.dental.service.UserService;

public class StaffFrame extends JFrame {
    private AppointmentService appointmentService;
    private UserService userService;
    private User loggedInUser;
    private JTextField idField = new JTextField();
    private JTextField patientNameField = new JTextField();
    private JTextField doctorField = new JTextField();
    private JTextField dateField = new JTextField();
    private JTextField timeField = new JTextField();
    private JTextArea outputArea = new JTextArea();

    public StaffFrame(AppointmentService appointmentService, UserService userService, User loggedInUser) {
        this.appointmentService = appointmentService;
        this.userService = userService;
        this.loggedInUser = loggedInUser;

        setTitle("Sunrise Dental Clinic - Staff");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLayout(new BorderLayout());

        JLabel userLabel = new JLabel("Logged in as: " + loggedInUser.getName() + " (" + loggedInUser.getRole() + ")");

        JPanel inputPanel = new JPanel(new GridLayout(7, 2));
        inputPanel.add(new JLabel("Appointment ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Patient name:"));
        inputPanel.add(patientNameField);
        inputPanel.add(new JLabel("Doctor:"));
        inputPanel.add(doctorField);
        inputPanel.add(new JLabel("Date:"));
        inputPanel.add(dateField);
        inputPanel.add(new JLabel("Time:"));
        inputPanel.add(timeField);

        JButton addButton = new JButton("Add Appointment");
        addButton.addActionListener(e -> addAppointment());
        inputPanel.add(addButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        inputPanel.add(logoutButton);

        inputPanel.add(new JLabel("Appointments:"));
        inputPanel.add(outputArea);

        add(userLabel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void addAppointment() {
        Appointment appointment = new Appointment(
                idField.getText(),
                patientNameField.getText(),
                doctorField.getText(),
                dateField.getText(),
                timeField.getText(),
                loggedInUser.getName());
        appointmentService.addAppointment(appointment);
        viewAppointments();
    }

    private void viewAppointments() {
        StringBuilder sb = new StringBuilder();
        List<Appointment> appointments = appointmentService.viewAppointments();
        if (appointments.isEmpty()) {
            outputArea.setText("No appointments yet.");
        } else {
            for (Appointment appointment : appointments) {
                sb.append(appointment.getId()).append(" | ")
                        .append(appointment.getPatientName()).append(" | ")
                        .append(appointment.getDoctor()).append(" | ")
                        .append(appointment.getDate()).append(" | ")
                        .append(appointment.getTime()).append("\n");
            }
            outputArea.setText(sb.toString());
        }
    }

    private void logout() {
        dispose();
        new LoginFrame(userService, appointmentService);
    }
}