package com.dental;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.dental.model.User;
import com.dental.service.AppointmentService;
import com.dental.service.AppointmentServiceImpl;
import com.dental.service.UserService;

public class LoginFrame extends JFrame {
    private UserService userService;
    private AppointmentService appointmentService;
    private JTextField usernameField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JLabel messageLabel = new JLabel(" ");

    public LoginFrame(UserService userService, AppointmentService appointmentService) {
        this.userService = userService;
        this.appointmentService = appointmentService;

        setTitle("Sunrise Dental Clinic - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 200);
        setResizable(false);
        setLayout(new GridLayout(0, 2));

        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> login());
        add(loginButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        add(exitButton);

        messageLabel.setForeground(Color.RED);
        add(messageLabel);

        setVisible(true);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Username and password cannot be empty.");
            return;
        }

        User user = userService.login(username, password);
        if (user == null) {
            messageLabel.setText("Invalid username or password. Try again.");
            return;
        }

        dispose();
        if (user.getRole().equals("Admin")) {
            new AdminFrame(appointmentService, userService);
        } else {
            new StaffFrame(appointmentService, userService, user);
        }
    }

    public static void main(String[] args) {
        new LoginFrame(new UserService(), new AppointmentServiceImpl());
    }
}