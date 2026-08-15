package com.dental;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.dental.model.Appointment;
import com.dental.model.Doctor;
import com.dental.model.TreatmentItem;
import com.dental.model.User;
import com.dental.service.AppointmentService;
import com.dental.service.DoctorService;
import com.dental.service.DoctorTreatmentService;
import com.dental.service.UserService;

public class AdminFrame extends JFrame {
    private UserService userService;
    private AppointmentService appointmentService;
    private DoctorService doctorService;
    private DoctorTreatmentService doctorTreatmentService;
    private DefaultTableModel staffTableModel;
    private DefaultTableModel logTableModel;
    private DefaultTableModel doctorTableModel;
    private DefaultTableModel treatmentTableModel;
    private JTable doctorTable;
    private String selectedDoctorId;
    private JTextField logSearchField = new JTextField();
    private String staffFilterId;
    private DefaultTableModel staffFilterTableModel;
    private JTable staffFilterTable;

    public AdminFrame(AppointmentService appointmentService, UserService userService,
                      DoctorService doctorService, DoctorTreatmentService doctorTreatmentService) {
        this.appointmentService = appointmentService;
        this.userService = userService;
        this.doctorService = doctorService;
        this.doctorTreatmentService = doctorTreatmentService;

        setTitle("Sunrise Dental Clinic - Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 620);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Staff Accounts", buildStaffPanel());
        tabs.addTab("Doctors", buildDoctorsPanel());
        tabs.addTab("Appointment Log", buildLogPanel());

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());

        JButton helpButton = new JButton("Help");
        helpButton.addActionListener(e -> showHelp());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(logoutButton);
        buttonPanel.add(helpButton);

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel buildStaffPanel() {
        staffTableModel = new DefaultTableModel(new String[]{"Staff ID", "Name", "Username", "Role"}, 0);
        JTable staffTable = new JTable(staffTableModel);
        loadStaffTable();

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

    private JPanel buildDoctorsPanel() {
        doctorTableModel = new DefaultTableModel(new String[]{"Doctor ID", "Name", "Consultation Fee (LKR)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        doctorTable = new JTable(doctorTableModel);
        loadDoctorTable();
        doctorTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadTreatmentsTable();
            }
        });

        JScrollPane doctorScroll = new JScrollPane(doctorTable);
        doctorScroll.setPreferredSize(new java.awt.Dimension(660, 140));

        treatmentTableModel = new DefaultTableModel(new String[]{"Treatment", "Price (LKR)"}, 0);
        JTable treatmentTable = new JTable(treatmentTableModel);
        JScrollPane treatmentScroll = new JScrollPane(treatmentTable);

        JLabel treatmentLabel = new JLabel("Select a doctor above, then set their treatment prices below:");

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadDoctorTable());

        JButton addButton = new JButton("Add Doctor");
        addButton.addActionListener(e -> addDoctor());

        JButton editFeeButton = new JButton("Edit Consultation Fee");
        editFeeButton.addActionListener(e -> editConsultationFee());

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> saveTreatments());

        JButton addRowButton = new JButton("Add Row");
        addRowButton.addActionListener(e -> treatmentTableModel.addRow(new Object[]{"", ""}));

        JButton removeRowButton = new JButton("Remove Row");
        removeRowButton.addActionListener(e -> removeTreatmentRow(treatmentTable));

        JButton resetButton = new JButton("Reset to Defaults");
        resetButton.addActionListener(e -> resetDefaults());

        JPanel doctorButtonPanel = new JPanel(new FlowLayout());
        doctorButtonPanel.add(refreshButton);
        doctorButtonPanel.add(addButton);
        doctorButtonPanel.add(editFeeButton);

        JPanel treatmentButtonPanel = new JPanel(new FlowLayout());
        treatmentButtonPanel.add(saveButton);
        treatmentButtonPanel.add(addRowButton);
        treatmentButtonPanel.add(removeRowButton);
        treatmentButtonPanel.add(resetButton);

        JPanel doctorPanel = new JPanel(new BorderLayout());
        doctorPanel.add(doctorScroll, BorderLayout.CENTER);
        doctorPanel.add(doctorButtonPanel, BorderLayout.SOUTH);

        JPanel treatmentPanel = new JPanel(new BorderLayout());
        treatmentPanel.add(treatmentLabel, BorderLayout.NORTH);
        treatmentPanel.add(treatmentScroll, BorderLayout.CENTER);
        treatmentPanel.add(treatmentButtonPanel, BorderLayout.SOUTH);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(doctorPanel, BorderLayout.NORTH);
        panel.add(treatmentPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildLogPanel() {
        logTableModel = new DefaultTableModel(new String[]{"Appointment ID", "Patient Name", "Doctor", "Date", "Time", "Handled By", "Staff ID", "Total (LKR)"}, 0);
        JTable logTable = new JTable(logTableModel);
        loadLogTable();

        JPanel searchPanel = new JPanel(new GridLayout(1, 2));
        searchPanel.add(new JLabel("Search (ID, patient name or date yyyy-MM-dd):"));
        searchPanel.add(logSearchField);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> loadLogTable());

        JButton refreshButton = new JButton("Show All");
        refreshButton.addActionListener(e -> {
            logSearchField.setText("");
            staffFilterId = null;
            staffFilterTable.clearSelection();
            loadLogTable();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(searchButton);
        buttonPanel.add(refreshButton);

        staffFilterTableModel = new DefaultTableModel(new String[]{"Staff ID", "Name"}, 0);
        staffFilterTable = new JTable(staffFilterTableModel);
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            staffFilterTableModel.addRow(new Object[]{user.getStaffId(), user.getName()});
        }
        staffFilterTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = staffFilterTable.getSelectedRow();
                staffFilterId = row == -1 ? null : (String) staffFilterTableModel.getValueAt(row, 0);
                loadLogTable();
            }
        });
        JScrollPane staffFilterScroll = new JScrollPane(staffFilterTable);
        staffFilterScroll.setPreferredSize(new Dimension(660, 140));

        JPanel staffFilterPanel = new JPanel(new BorderLayout());
        staffFilterPanel.add(new JLabel("Click a staff member below to see only their appointments:"), BorderLayout.NORTH);
        staffFilterPanel.add(staffFilterScroll, BorderLayout.CENTER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(logTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(staffFilterPanel, BorderLayout.PAGE_END);
        return panel;
    }

    private void loadStaffTable() {
        staffTableModel.setRowCount(0);
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            staffTableModel.addRow(new Object[]{user.getStaffId(), user.getName(), user.getUsername(), user.getRole()});
        }
    }

    private void loadDoctorTable() {
        doctorTableModel.setRowCount(0);
        List<Doctor> doctors = doctorService.getAllDoctors();
        for (Doctor doctor : doctors) {
            doctorTableModel.addRow(new Object[]{doctor.getId(), doctor.getName(), doctor.getConsultationFee()});
        }
    }

    private void loadTreatmentsTable() {
        treatmentTableModel.setRowCount(0);
        int row = doctorTable.getSelectedRow();
        if (row == -1) {
            selectedDoctorId = null;
            return;
        }
        selectedDoctorId = (String) doctorTableModel.getValueAt(row, 0);
        List<TreatmentItem> treatments = doctorTreatmentService.getAllForDoctor(selectedDoctorId);
        for (TreatmentItem treatment : treatments) {
            treatmentTableModel.addRow(new Object[]{treatment.getName(), treatment.getCost()});
        }
    }

    private void loadLogTable() {
        logTableModel.setRowCount(0);
        List<Appointment> appointments;
        if (staffFilterId != null) {
            appointments = appointmentService.searchByStaffId(staffFilterId);
        } else {
            appointments = appointmentService.search(logSearchField.getText().trim());
        }
        for (Appointment appointment : appointments) {
            logTableModel.addRow(new Object[]{
                    appointment.getId(),
                    appointment.getPatientName(),
                    appointment.getDoctor(),
                    appointment.getDate(),
                    appointment.getTime(),
                    appointment.getHandledBy(),
                    appointment.getHandledById(),
                    appointment.getTotal()
            });
        }
    }

    private void addStaff() {
        String[] values = showForm("Add Staff",
                new String[]{"Staff ID:", "Name:", "Username:", "Password:", "Role (Staff or Admin):"},
                new String[]{"", "", "", "", "Staff"});
        if (values == null) {
            return;
        }
        for (String value : values) {
            if (value.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Missing Details", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        userService.addUser(new User(values[0], values[1], values[2], values[3], values[4]));
        loadStaffTable();
    }

    private void editStaff(JTable staffTable) {
        int row = staffTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a staff member to edit.");
            return;
        }
        String staffId = (String) staffTableModel.getValueAt(row, 0);
        String[] values = showForm("Edit Staff",
                new String[]{"Name:", "Username:", "Password:", "Role (Staff or Admin):"},
                new String[]{
                        String.valueOf(staffTableModel.getValueAt(row, 1)),
                        String.valueOf(staffTableModel.getValueAt(row, 2)),
                        "",
                        String.valueOf(staffTableModel.getValueAt(row, 3))
                });
        if (values == null) {
            return;
        }
        for (String value : values) {
            if (value.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Missing Details", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        userService.editUser(new User(staffId, values[0], values[1], values[2], values[3]));
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

    private void addDoctor() {
        String[] values = showForm("Add Doctor",
                new String[]{"Doctor name:", "Consultation fee (LKR):"},
                new String[]{"", ""});
        if (values == null) {
            return;
        }
        if (values[0].isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the doctor name.", "Missing Details", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double fee = parseAmount(values[1]);
        if (fee < 0) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.");
            return;
        }
        String newId = doctorService.addDoctor(values[0], fee);
        loadDoctorTable();
        for (int i = 0; i < doctorTableModel.getRowCount(); i++) {
            if (doctorTableModel.getValueAt(i, 0).equals(newId)) {
                doctorTable.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    private void editConsultationFee() {
        int row = doctorTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a doctor to edit.");
            return;
        }
        String doctorId = (String) doctorTableModel.getValueAt(row, 0);
        String[] values = showForm("Edit Consultation Fee",
                new String[]{"New consultation fee (LKR):"},
                new String[]{String.valueOf(doctorTableModel.getValueAt(row, 2))});
        if (values == null) {
            return;
        }
        double fee = parseAmount(values[0]);
        if (fee < 0) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.");
            return;
        }
        doctorService.editConsultationFee(doctorId, fee);
        loadDoctorTable();
    }

    private void saveTreatments() {
        if (selectedDoctorId == null) {
            JOptionPane.showMessageDialog(this, "Select a doctor first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<TreatmentItem> treatments = new ArrayList<>();
        for (int i = 0; i < treatmentTableModel.getRowCount(); i++) {
            String name = String.valueOf(treatmentTableModel.getValueAt(i, 0)).trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Row " + (i + 1) + " has no treatment name. Remove empty rows or fill them in.", "Missing Details", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double price = parseAmount(String.valueOf(treatmentTableModel.getValueAt(i, 1)));
            if (price < 0) {
                JOptionPane.showMessageDialog(this, "Row " + (i + 1) + " has an invalid price.", "Invalid Price", JOptionPane.WARNING_MESSAGE);
                return;
            }
            treatments.add(new TreatmentItem(name, price));
        }
        doctorTreatmentService.updateTreatments(selectedDoctorId, treatments);
        loadTreatmentsTable();
        JOptionPane.showMessageDialog(this, "Treatment prices saved.", "Saved", JOptionPane.INFORMATION_MESSAGE);
    }

    private void removeTreatmentRow(JTable treatmentTable) {
        int row = treatmentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        treatmentTableModel.removeRow(row);
    }

    private void resetDefaults() {
        if (selectedDoctorId == null) {
            JOptionPane.showMessageDialog(this, "Select a doctor first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        doctorTreatmentService.populateDefaults(selectedDoctorId);
        loadTreatmentsTable();
        JOptionPane.showMessageDialog(this, "Default treatments restored.", "Reset", JOptionPane.INFORMATION_MESSAGE);
    }

    private double parseAmount(String input) {
        try {
            return Double.parseDouble(input.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String[] showForm(String title, String[] labels, String[] initialValues) {
        JPanel panel = new JPanel(new GridLayout(labels.length, 2));
        JTextField[] fields = new JTextField[labels.length];
        for (int i = 0; i < labels.length; i++) {
            panel.add(new JLabel(labels[i]));
            fields[i] = new JTextField(initialValues[i]);
            panel.add(fields[i]);
        }
        int result = JOptionPane.showConfirmDialog(this, panel, title, JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }
        String[] values = new String[labels.length];
        for (int i = 0; i < labels.length; i++) {
            values[i] = fields[i].getText().trim();
        }
        return values;
    }

    private void logout() {
        dispose();
        new LauncherFrame(userService, appointmentService, doctorService, doctorTreatmentService);
    }

    private void showHelp() {
        JOptionPane.showMessageDialog(this,
                "Admin help:\n\n"
                        + "Staff Accounts tab:\n"
                        + "  - View All Staff - refresh the staff list\n"
                        + "  - Add Staff - create a new staff account (one form with all details)\n"
                        + "  - Edit Staff / Remove Staff - manage existing accounts\n\n"
                        + "Doctors tab:\n"
                        + "  - Add Doctor - add a doctor; their default treatment price list is filled in automatically\n"
                        + "  - Edit Consultation Fee - change the doctor's consultation fee\n"
                        + "  - Select a doctor, then edit their treatment prices in the table below:\n"
                        + "      Save Changes - saves the prices for the selected doctor\n"
                        + "      Add Row / Remove Row - add or delete treatments for that doctor\n"
                        + "      Reset to Defaults - restores the standard treatment list and prices\n\n"
                        + "Appointment Log tab:\n"
                        + "  - Shows every appointment, its doctor, items total, and who handled it\n"
                        + "  - Search - find by appointment ID, patient name, or a date (yyyy-MM-dd)\n"
                        + "  - Click a staff member at the bottom to show only their appointments\n"
                        + "  - Show All - clears the search and lists everything\n\n"
                        + "Logout - return to the interface chooser.");
    }
}