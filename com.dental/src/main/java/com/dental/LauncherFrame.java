package com.dental;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.dental.model.User;
import com.dental.service.AppointmentService;
import com.dental.service.AppointmentServiceImpl;
import com.dental.service.DoctorService;
import com.dental.service.DoctorTreatmentService;
import com.dental.service.UserService;

public class LauncherFrame extends JFrame {
    private UserService userService;
    private AppointmentService appointmentService;
    private DoctorService doctorService;
    private DoctorTreatmentService doctorTreatmentService;

    public LauncherFrame(UserService userService, AppointmentService appointmentService,
                         DoctorService doctorService, DoctorTreatmentService doctorTreatmentService) {
        this.userService = userService;
        this.appointmentService = appointmentService;
        this.doctorService = doctorService;
        this.doctorTreatmentService = doctorTreatmentService;

        setTitle("Sunrise Dental Clinic - Choose Interface");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 220);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(0, 1));

        add(new JLabel("Which interface do you want to open?"));

        JButton userButton = new JButton("User Interface");
        userButton.addActionListener(e -> openUserInterface());
        add(userButton);

        JButton adminButton = new JButton("Admin Interface");
        adminButton.addActionListener(e -> openAdminInterface());
        add(adminButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        add(exitButton);

        JPanel spacer = new JPanel();
        add(spacer);

        setVisible(true);
    }

    private void openUserInterface() {
        User user = userService.findByUsername("user@sunshine.lk");
        if (user == null) {
            user = new User("S001", "User", "user@sunshine.lk", "1234", "Staff");
        }
        dispose();
        new StaffFrame(appointmentService, userService, doctorService, doctorTreatmentService, user);
    }

    private void openAdminInterface() {
        dispose();
        new AdminFrame(appointmentService, userService, doctorService, doctorTreatmentService);
    }

    public static void main(String[] args) {
        new LauncherFrame(new UserService(), new AppointmentServiceImpl(), new DoctorService(), new DoctorTreatmentService());
    }
}