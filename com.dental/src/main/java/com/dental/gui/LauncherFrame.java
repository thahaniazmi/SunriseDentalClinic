package com.dental.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.dental.PinkButton;
import com.dental.UIStyles;
import com.dental.client.ApiClient;
import com.dental.model.User;
import com.dental.server.DentalServer;

public class LauncherFrame extends JFrame {
    private ApiClient api;

    public LauncherFrame(ApiClient api) {
        this.api = api;

        applyTheme();

        setTitle("Sunrise Dental Clinic - Choose Interface");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(440, 420);
        setMinimumSize(new Dimension(440, 420));
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel titleLabel = new JLabel("Sunrise Dental Clinic");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(UIStyles.TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel questionLabel = new JLabel("Which interface do you want to open?");
        questionLabel.setForeground(UIStyles.TEXT_SECONDARY);
        questionLabel.setHorizontalAlignment(JLabel.CENTER);
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton userButton = new PinkButton("User Interface");
        userButton.addActionListener(e -> openUserInterface());
        sizeLauncherButton(userButton);

        JButton adminButton = new PinkButton("Admin Interface");
        adminButton.addActionListener(e -> openAdminInterface());
        sizeLauncherButton(adminButton);

        JButton exitButton = new PinkButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        sizeLauncherButton(exitButton);

        content.add(Box.createVerticalGlue());
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(10));
        content.add(questionLabel);
        content.add(Box.createVerticalStrut(24));
        content.add(userButton);
        content.add(Box.createVerticalStrut(12));
        content.add(adminButton);
        content.add(Box.createVerticalStrut(12));
        content.add(exitButton);
        content.add(Box.createVerticalGlue());

        setContentPane(content);

        setVisible(true);
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

    private static void sizeLauncherButton(JButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(340, 50));
        button.setMinimumSize(new Dimension(340, 50));
        button.setMaximumSize(new Dimension(340, 50));
    }

    private void openUserInterface() {
        try {
            User user = api.findByUsername("user@sunshine.lk");
            if (user == null) {
                user = new User("S001", "User", "user@sunshine.lk", "1234", "Staff");
            }
            dispose();
            new StaffFrame(api, user);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not open the User Interface.\nIs the DentalServer running? (" + ex.getMessage() + ")",
                    "Server Unreachable", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openAdminInterface() {
        try {
            dispose();
            new AdminFrame(api);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not open the Admin Interface.\nIs the DentalServer running? (" + ex.getMessage() + ")",
                    "Server Unreachable", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // embedded server so the whole app runs from this single entry point
        DentalServer.start();
        new LauncherFrame(new ApiClient());
    }
}
