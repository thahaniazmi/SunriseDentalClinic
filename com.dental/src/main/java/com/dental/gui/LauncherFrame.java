package com.dental.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.dental.PinkButton;
import com.dental.UIStyles;
import com.dental.client.ApiClient;
import com.dental.model.User;
import com.dental.server.DentalServer;

public class LauncherFrame extends JFrame {
    private ApiClient api;
    private JTextField usernameField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();

    public LauncherFrame(ApiClient api) {
        this.api = api;

        applyTheme();

        setTitle("Sunrise Dental Clinic - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(24, 40, 24, 40));

        JLabel titleLabel = new JLabel("Sunrise Dental Clinic");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(UIStyles.TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel questionLabel = new JLabel("Please log in to continue.");
        questionLabel.setForeground(UIStyles.TEXT_SECONDARY);
        questionLabel.setHorizontalAlignment(JLabel.CENTER);
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        UIStyles.styleField(usernameField);
        UIStyles.styleField(passwordField);

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        UIStyles.styleFieldLabel(usernameLabel);
        UIStyles.styleFieldLabel(passwordLabel);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 12, 12));
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);

        JButton loginButton = new PinkButton("Login");
        loginButton.addActionListener(e -> login());
        sizeLoginButton(loginButton);

        JButton exitButton = new PinkButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        sizeLoginButton(exitButton);

        content.add(Box.createVerticalGlue());
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(8));
        content.add(questionLabel);
        content.add(Box.createVerticalStrut(20));
        content.add(formPanel);
        content.add(Box.createVerticalStrut(18));
        content.add(loginButton);
        content.add(Box.createVerticalStrut(10));
        content.add(exitButton);
        content.add(Box.createVerticalGlue());

        setContentPane(content);

        pack();
        setLocationRelativeTo(null);

        setVisible(true);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the username and password.", "Missing Details",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = api.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            JOptionPane.showMessageDialog(this, "Wrong username or password.", "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        dispose();
        if (user.getRole().equalsIgnoreCase("Admin")) {
            new AdminFrame(api, user);
        } else {
            new StaffFrame(api, user);
        }
    }

    private static void sizeLoginButton(JButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(340, 46));
        button.setMinimumSize(new Dimension(340, 46));
        button.setMaximumSize(new Dimension(340, 46));
    }

    public static void applyTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception ignored) {
            }
        }
        Font normalFont = new Font("Segoe UI", Font.PLAIN, 15);

        // bigger fonts everywhere
        UIManager.put("defaultFont", normalFont);
        UIManager.put("Label.font", normalFont);
        UIManager.put("Button.font", normalFont);
        UIManager.put("TextField.font", normalFont);
        UIManager.put("PasswordField.font", normalFont);
        UIManager.put("ComboBox.font", normalFont);
        UIManager.put("CheckBox.font", normalFont);
        UIManager.put("Table.font", normalFont);
        UIManager.put("TabbedPane.font", normalFont);
        UIManager.put("OptionPane.messageFont", normalFont);
        UIManager.put("OptionPane.buttonFont", normalFont);
        UIManager.put("Spinner.font", normalFont);
        UIManager.put("TextArea.font", normalFont);
        UIManager.put("List.font", normalFont);

        // our colours
        UIManager.put("Panel.background", UIStyles.BACKGROUND);
        UIManager.put("Table.background", UIStyles.BACKGROUND);
        UIManager.put("Table.foreground", UIStyles.TEXT_PRIMARY);
        UIManager.put("Table.alternateRowColor", UIStyles.ALTERNATE_ROW);
        UIManager.put("Table.gridColor", UIStyles.BORDER_COLOR);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", true);
        UIManager.put("Table.selectionBackground", UIStyles.SELECTION);
        UIManager.put("Table.selectionForeground", UIStyles.TEXT_PRIMARY);
        UIManager.put("Table.rowHeight", 30);
        UIManager.put("TableHeader.background", UIStyles.PINK);
        UIManager.put("TableHeader.foreground", Color.WHITE);
        UIManager.put("TableHeader.font", UIStyles.FONT_BOLD);
        UIManager.put("Label.foreground", UIStyles.TEXT_PRIMARY);
        UIManager.put("TextField.inactiveForeground", UIStyles.PLACEHOLDER);
        UIManager.put("Button.margin", new Insets(10, 16, 10, 16));
    }

    public static void main(String[] args) {
        // embedded server so the whole app runs from this single entry point
        DentalServer.start();
        new LauncherFrame(new ApiClient());
    }
}
