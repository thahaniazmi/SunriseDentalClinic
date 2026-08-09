package com.dental;

import java.util.List;
import java.util.Scanner;

import com.dental.model.Appointment;
import com.dental.service.AppointmentService;
import com.dental.service.AppointmentServiceImpl;
import com.dental.service.AuthService;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AuthService authService = new AuthService();
        AppointmentService appointmentService = new AppointmentServiceImpl();
        boolean loggedIn = false;
        boolean running = true;

        while (running) {
            System.out.println("\n--- Sunrise Dental Clinic ---");
            System.out.println("1. Login");
            System.out.println("2. Add Appointment");
            System.out.println("3. View Appointment");
            System.out.println("4. Generate Bill");
            System.out.println("5. Help");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    System.out.print("Username: ");
                    String username = scanner.nextLine();
                    System.out.print("Password: ");
                    String password = scanner.nextLine();
                    if (authService.login(username, password)) {
                        loggedIn = true;
                        System.out.println("Login successful. Welcome, " + username + "!");
                    } else {
                        System.out.println("Invalid username or password.");
                    }
                    break;
                case 2:
                    if (!loggedIn) {
                        System.out.println("Please login first.");
                        break;
                    }
                    System.out.print("Appointment ID: ");
                    String id = scanner.nextLine();
                    System.out.print("Patient name: ");
                    String patientName = scanner.nextLine();
                    System.out.print("Doctor name: ");
                    String doctor = scanner.nextLine();
                    System.out.print("Date (e.g. 2026-08-10): ");
                    String date = scanner.nextLine();
                    System.out.print("Time (e.g. 09:30): ");
                    String time = scanner.nextLine();
                    appointmentService.addAppointment(new Appointment(id, patientName, doctor, date, time));
                    System.out.println("Appointment added.");
                    break;
                case 3:
                    if (!loggedIn) {
                        System.out.println("Please login first.");
                        break;
                    }
                    List<Appointment> appointments = appointmentService.getAllAppointments();
                    if (appointments.isEmpty()) {
                        System.out.println("No appointments yet.");
                    } else {
                        for (Appointment appointment : appointments) {
                            appointment.display();
                        }
                    }
                    break;
                case 4:
                    System.out.println("Bill generation coming soon.");
                    break;
                case 5:
                    System.out.println("1. Login - sign in to the system");
                    System.out.println("2. Add Appointment - register a new appointment");
                    System.out.println("3. View Appointment - see all registered appointments");
                    System.out.println("4. Generate Bill - calculate the bill");
                    System.out.println("5. Help - show this message");
                    System.out.println("6. Exit - close the program");
                    break;
                case 6:
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }

        scanner.close();
    }
}