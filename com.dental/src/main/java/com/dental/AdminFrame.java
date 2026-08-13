package com.dental;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.dental.model.Appointment;
import com.dental.model.User;
import com.dental.service.AppointmentService;
import com.dental.service.UserService;

public class AdminFrame extends JFrame {
    private UserService userService;
    private AppointmentService appointmentService;
    private DefaultTableModel staffTableModel;
    private DefaultTableModel logTableModel;

    public AdminFrame(AppointmentService appointmentService, UserService userService) {
        this.appointmentService = appointmentService;
        this.userService = userService;

        setTitle("Sunrise Dental Clinic - Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Staff Accounts", buildStaffPanel());
        tabs.addTab("Appointment Log", buildLogPanel());

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(logoutButton);

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel buildStaffPanel() {
        staffTableModel = new DefaultTableModel(new String[]{"Staff ID", "Name", "Username", "Role"}, 0);
        JTable staffTable = new JTable(staffTableModel);
        loadStaffTable();

        staffTable.getSelectionModel().addListSelectionListener(e -> loadStaffTable());

        JButton viewButton = new JButton("View All Staff");
        viewButton.addActionListener(e -> loadStaffTable());

        JButton addButton = new JButton("Add Staff");
        addButton.addActionListener(e -> addStaff());

        JButton editButton = new JButton("Edit Staff");
        editButton.addActionListener(e -> editStaff(staffTable));

        JButton removeButton = new JButton("Remove Staff");
        removeButton.addActionListener(e -> removeStaff(staffTable));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(viewButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(removeButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(staffTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildLogPanel() {
        logTableModel = new DefaultTableModel(new String[]{"Appointment ID", "Patient Name", "Date", "Time", "Handled By"}, 0);
        JTable logTable = new JTable(logTableModel);
        loadLogTable();

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadLogTable());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(refreshButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(logTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadStaffTable() {
        staffTableModel.setRowCount(0);
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            staffTableModel.addRow(new Object[]{user.getStaffId(), user.getName(), user.getUsername(), user.getRole()});
        }
    }

    private void loadLogTable() {
        logTableModel.setRowCount(0);
        List<Appointment> appointments = appointmentService.viewAppointments();
        for (Appointment appointment : appointments) {
            logTableModel.addRow(new Object[]{
                    appointment.getId(),
                    appointment.getPatientName(),
                    appointment.getDate(),
                    appointment.getTime(),
                    appointment.getHandledBy()
            });
        }
    }

    private void addStaff() {
        String staffId = JOptionPane.showInputDialog(this, "Staff ID:");
        if (staffId == null || staffId.isEmpty()) {
            return;
        }
        String name = JOptionPane.showInputDialog(this, "Name:");
        if (name == null || name.isEmpty()) {
            return;
        }
        String username = JOptionPane.showInputDialog(this, "Username:");
        if (username == null || username.isEmpty()) {
            return;
        }
        String password = JOptionPane.showInputDialog(this, "Password:");
        if (password == null || password.isEmpty()) {
            return;
        }
        String role = JOptionPane.showInputDialog(this, "Role (Staff or Admin):");
        if (role == null || role.isEmpty()) {
            return;
        }
        userService.addUser(new User(staffId, name, username, password, role));
        loadStaffTable();
    }

    private void editStaff(JTable staffTable) {
        int row = staffTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a staff member to edit.");
            return;
        }
        String staffId = (String) staffTableModel.getValueAt(row, 0);
        String name = JOptionPane.showInputDialog(this, "Name:", staffTableModel.getValueAt(row, 1));
        if (name == null || name.isEmpty()) {
            return;
        }
        String username = JOptionPane.showInputDialog(this, "Username:", staffTableModel.getValueAt(row, 2));
        if (username == null || username.isEmpty()) {
            return;
        }
        String password = JOptionPane.showInputDialog(this, "Password:");
        if (password == null || password.isEmpty()) {
            return;
        }
        String role = JOptionPane.showInputDialog(this, "Role (Staff or Admin):", staffTableModel.getValueAt(row, 3));
        if (role == null || role.isEmpty()) {
            return;
        }
        userService.editUser(new User(staffId, name, username, password, role));
        loadStaffTable();
    }

    private void removeStaff(JTable staffTable) {
        int row = staffTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a staff member to remove.");
            return;
        }
        String staffId = (String) staffTableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Remove this staff member?");
        if (confirm == JOptionPane.YES_OPTION) {
            userService.removeUser(staffId);
            loadStaffTable();
        }
    }

    private void logout() {
        dispose();
        new LoginFrame(userService, appointmentService);
    }
}