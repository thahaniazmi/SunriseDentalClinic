package com.dental.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.dental.PinkButton;
import com.dental.UIStyles;
import com.dental.client.ApiClient;
import com.dental.model.Appointment;
import com.dental.model.Doctor;
import com.dental.model.TreatmentItem;
import com.dental.model.User;

public class AdminFrame extends JFrame {
    private ApiClient api;
    private DefaultTableModel staffTableModel;
    private DefaultTableModel logTableModel;
    private DefaultTableModel doctorTableModel;
    private DefaultTableModel treatmentTableModel;
    private JTable staffTable;
    private JTable logTable;
    private JTable doctorTable;
    private JTable treatmentTable;
    private String selectedDoctorId;
    private JTextField logSearchField = new JTextField();
    private String staffFilterId;
    private DefaultTableModel staffFilterTableModel;
    private JTable staffFilterTable;
    private JPanel treatmentArea;
    private JLabel todayCount;
    private JLabel weekCount;
    private JLabel monthCount;
    private JLabel allTimeCount;
    private DefaultTableModel dentistTableModel;
    private JTable dentistReportTable;

    public AdminFrame(ApiClient api, User loggedInUser) {
        this.api = api;

        setTitle("Sunrise Dental Clinic - Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1040, 860);
        setMinimumSize(new Dimension(940, 700));
        setLocationRelativeTo(null);

        UIStyles.styleField(logSearchField);

        JLabel userLabel = new JLabel("Welcome, " + loggedInUser.getName());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userLabel.setForeground(UIStyles.TEXT_PRIMARY);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(10, 16, 10, 16));
        topPanel.add(userLabel, BorderLayout.WEST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Reports", buildReportsPanel());
        tabs.addTab("Staff Accounts", buildStaffPanel());
        tabs.addTab("Doctors", buildDoctorsPanel());
        tabs.addTab("Appointment Log", buildLogPanel());

        JButton logoutButton = new PinkButton("Logout");
        logoutButton.addActionListener(e -> logout());

        JButton helpButton = new PinkButton("Help");
        helpButton.addActionListener(e -> showHelp());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(logoutButton);
        buttonPanel.add(helpButton);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private JPanel buildStaffPanel() {
        staffTableModel = new DefaultTableModel(new String[]{"Staff ID", "Name", "Username", "Role"}, 0);
        staffTable = new JTable(staffTableModel);
        UIStyles.styleTable(staffTable);
        loadStaffTable();

        JButton viewButton = new PinkButton("View All Staff");
        viewButton.addActionListener(e -> refreshStaffList());

        JButton addButton = new PinkButton("Add Staff");
        addButton.addActionListener(e -> addStaff());

        JButton editButton = new PinkButton("Edit Staff");
        editButton.addActionListener(e -> editStaff(staffTable));

        JButton removeButton = new PinkButton("Remove Staff");
        removeButton.addActionListener(e -> removeStaff(staffTable));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(viewButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(removeButton);

        JScrollPane staffScroll = new JScrollPane(staffTable);
        staffScroll.setPreferredSize(new Dimension(940, 190));
        staffScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));
        staffScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel staffCard = new JPanel(new BorderLayout());
        staffCard.add(staffScroll, BorderLayout.CENTER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(12, 16, 12, 16));
        panel.add(staffCard, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshStaffList() {
        // just reloads the staff list
        loadStaffTable();
    }

    private JPanel buildDoctorsPanel() {
        doctorTableModel = new DefaultTableModel(new String[]{"Doctor ID", "Name", "Consultation Fee (LKR)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        doctorTable = new JTable(doctorTableModel);
        UIStyles.styleTable(doctorTable);
        loadDoctorTable();
        doctorTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadTreatmentsTable();
            }
        });

        JScrollPane doctorScroll = new JScrollPane(doctorTable);
        doctorScroll.setPreferredSize(new Dimension(940, 160));
        doctorScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        doctorScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        treatmentTableModel = new DefaultTableModel(new String[]{"Treatment", "Price (LKR)"}, 0);
        treatmentTable = new JTable(treatmentTableModel);
        UIStyles.styleTable(treatmentTable);
        JScrollPane treatmentScroll = new JScrollPane(treatmentTable);
        treatmentScroll.setPreferredSize(new Dimension(940, 160));
        treatmentScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        treatmentScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel treatmentLabel = new JLabel("Select a doctor above, then set their treatment prices below:");
        UIStyles.styleFieldLabel(treatmentLabel);

        JButton refreshButton = new PinkButton("Refresh");
        refreshButton.addActionListener(e -> loadDoctorTable());

        JButton addButton = new PinkButton("Add Doctor");
        addButton.addActionListener(e -> addDoctor());

        JButton editFeeButton = new PinkButton("Edit Consultation Fee");
        editFeeButton.addActionListener(e -> editConsultationFee());

        JButton saveButton = new PinkButton("Save Changes");
        saveButton.addActionListener(e -> saveTreatments());

        JButton addRowButton = new PinkButton("Add Row");
        addRowButton.addActionListener(e -> treatmentTableModel.addRow(new Object[]{"", ""}));

        JButton removeRowButton = new PinkButton("Remove Row");
        removeRowButton.addActionListener(e -> removeTreatmentRow(treatmentTable));

        JPanel doctorButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        doctorButtonPanel.add(refreshButton);
        doctorButtonPanel.add(addButton);
        doctorButtonPanel.add(editFeeButton);

        JPanel treatmentButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        treatmentButtonPanel.add(saveButton);
        treatmentButtonPanel.add(addRowButton);
        treatmentButtonPanel.add(removeRowButton);

        JPanel doctorPanel = new JPanel(new BorderLayout());
        doctorPanel.add(doctorScroll, BorderLayout.CENTER);
        doctorPanel.add(doctorButtonPanel, BorderLayout.SOUTH);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.add(treatmentScroll, BorderLayout.CENTER);

        JLabel placeholderLabel = new JLabel("Select a doctor above to edit their prices", JLabel.CENTER);
        placeholderLabel.setForeground(UIStyles.PLACEHOLDER);
        placeholderLabel.setFont(placeholderLabel.getFont().deriveFont(Font.ITALIC));

        treatmentArea = new JPanel(new CardLayout());
        treatmentArea.add(placeholderLabel, "empty");
        treatmentArea.add(tableCard, "table");
        updateTreatmentAreaState();

        JPanel treatmentPanel = new JPanel(new BorderLayout());
        treatmentPanel.add(treatmentLabel, BorderLayout.NORTH);
        treatmentPanel.add(treatmentArea, BorderLayout.CENTER);
        treatmentPanel.add(treatmentButtonPanel, BorderLayout.SOUTH);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(12, 16, 12, 16));
        panel.add(doctorPanel, BorderLayout.NORTH);
        panel.add(treatmentPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildLogPanel() {
        logTableModel = new DefaultTableModel(new String[]{"Appointment ID", "Patient Name", "Doctor", "Date", "Time", "Handled By", "Staff ID", "Total (LKR)"}, 0);
        logTable = new JTable(logTableModel);
        UIStyles.styleTable(logTable);
        loadLogTable();

        JLabel searchLabel = new JLabel("Search (ID, patient name or date yyyy-MM-dd):");
        UIStyles.styleFieldLabel(searchLabel);
        JPanel searchPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        searchPanel.add(searchLabel);
        searchPanel.add(logSearchField);

        JButton searchButton = new PinkButton("Search");
        searchButton.addActionListener(e -> loadLogTable());

        JButton refreshButton = new PinkButton("Show All");
        refreshButton.addActionListener(e -> {
            logSearchField.setText("");
            staffFilterId = null;
            staffFilterTable.clearSelection();
            loadLogTable();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(searchButton);
        buttonPanel.add(refreshButton);

        staffFilterTableModel = new DefaultTableModel(new String[]{"Staff ID", "Name"}, 0);
        staffFilterTable = new JTable(staffFilterTableModel);
        UIStyles.styleTable(staffFilterTable);
        // clicking a staff member filters the log above
        List<User> users = api.getAllUsers();
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
        styleTable(staffFilterTable);
        JScrollPane staffFilterScroll = new JScrollPane(staffFilterTable);
        staffFilterScroll.setPreferredSize(new Dimension(940, 190));
        staffFilterScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));
        staffFilterScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel staffFilterLabel = new JLabel("Click a staff member below to see only their appointments:");
        UIStyles.styleFieldLabel(staffFilterLabel);
        JPanel staffFilterPanel = new JPanel(new BorderLayout());
        staffFilterPanel.add(staffFilterLabel, BorderLayout.NORTH);
        staffFilterPanel.add(staffFilterScroll, BorderLayout.CENTER);

        JScrollPane logScroll = new JScrollPane(logTable);
        logScroll.setPreferredSize(new Dimension(940, 340));
        logScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));
        logScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel logCard = new JPanel(new BorderLayout());
        logCard.add(logScroll, BorderLayout.CENTER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(logCard, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(staffFilterPanel, BorderLayout.PAGE_END);
        return panel;
    }

    private JPanel buildReportsPanel() {
        todayCount = new JLabel("0", JLabel.CENTER);
        weekCount = new JLabel("0", JLabel.CENTER);
        monthCount = new JLabel("0", JLabel.CENTER);
        allTimeCount = new JLabel("0", JLabel.CENTER);

        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 12, 12));
        summaryPanel.add(summaryCard(todayCount, "Appointments Today"));
        summaryPanel.add(summaryCard(weekCount, "Appointments This Week"));
        summaryPanel.add(summaryCard(monthCount, "Appointments This Month"));
        summaryPanel.add(summaryCard(allTimeCount, "Appointments All Time"));

        dentistTableModel = new DefaultTableModel(new String[]{"Dentist", "Appointments This Month"}, 0);
        dentistReportTable = new JTable(dentistTableModel);
        UIStyles.styleTable(dentistReportTable);

        JScrollPane dentistScroll = new JScrollPane(dentistReportTable);
        dentistScroll.setPreferredSize(new Dimension(940, 220));
        dentistScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JLabel dentistLabel = new JLabel("Dentist breakdown (this month):");
        UIStyles.styleFieldLabel(dentistLabel);

        JPanel dentistSection = new JPanel(new BorderLayout());
        dentistSection.add(dentistLabel, BorderLayout.NORTH);
        dentistSection.add(dentistScroll, BorderLayout.CENTER);

        JButton refreshButton = new PinkButton("Refresh");
        refreshButton.addActionListener(e -> loadReports());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(refreshButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(12, 16, 12, 16));
        panel.add(summaryPanel, BorderLayout.NORTH);
        panel.add(dentistSection, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        loadReports();
        return panel;
    }

    // one small card showing a number with a title under it
    private JPanel summaryCard(JLabel countLabel, String title) {
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        countLabel.setForeground(UIStyles.PINK);

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        UIStyles.styleFieldLabel(titleLabel);

        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        card.add(countLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);
        return card;
    }

    private void loadReports() {
        LocalDate today = LocalDate.now();
        String todayText = today.toString();
        String weekStart = today.with(DayOfWeek.MONDAY).toString();
        String weekEnd = today.with(DayOfWeek.MONDAY).plusDays(6).toString();
        String monthStart = today.withDayOfMonth(1).toString();
        String monthEnd = today.withDayOfMonth(today.lengthOfMonth()).toString();

        todayCount.setText(String.valueOf(api.countAppointments(todayText, todayText, null)));
        weekCount.setText(String.valueOf(api.countAppointments(weekStart, weekEnd, null)));
        monthCount.setText(String.valueOf(api.countAppointments(monthStart, monthEnd, null)));
        allTimeCount.setText(String.valueOf(api.countAppointments("0000-01-01", "9999-12-31", null)));

        dentistTableModel.setRowCount(0);
        for (Doctor doctor : api.getDoctors()) {
            int count = api.countAppointments(monthStart, monthEnd, doctor.getName());
            dentistTableModel.addRow(new Object[]{doctor.getName(), count});
        }
    }

    private void styleTable(JTable table) {
        for (int c = 0; c < table.getColumnCount(); c++) {
            int width = 60;
            Component header = table.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(
                    table, table.getColumnName(c), false, false, -1, c);
            width = Math.max(width, header.getPreferredSize().width + 12);
            for (int r = 0; r < table.getRowCount(); r++) {
                Component cell = table.getDefaultRenderer(table.getColumnClass(c)).getTableCellRendererComponent(
                        table, table.getValueAt(r, c), false, false, r, c);
                width = Math.max(width, cell.getPreferredSize().width + 12);
            }
            table.getColumnModel().getColumn(c).setPreferredWidth(Math.min(width, 280));
        }
    }

    private void updateTreatmentAreaState() {
        if (treatmentArea == null) {
            return;
        }
        CardLayout layout = (CardLayout) treatmentArea.getLayout();
        layout.show(treatmentArea, selectedDoctorId == null ? "empty" : "table");
    }

    private void loadStaffTable() {
        staffTableModel.setRowCount(0);
        List<User> users = api.getAllUsers();
        for (User user : users) {
            staffTableModel.addRow(new Object[]{user.getStaffId(), user.getName(), user.getUsername(), user.getRole()});
        }
        styleTable(staffTable);
    }

    private void loadDoctorTable() {
        doctorTableModel.setRowCount(0);
        List<Doctor> doctors = api.getDoctors();
        for (Doctor doctor : doctors) {
            doctorTableModel.addRow(new Object[]{doctor.getId(), doctor.getName(), doctor.getConsultationFee()});
        }
        styleTable(doctorTable);
    }

    private void loadTreatmentsTable() {
        treatmentTableModel.setRowCount(0);
        int row = doctorTable.getSelectedRow();
        if (row == -1) {
            selectedDoctorId = null;
            updateTreatmentAreaState();
            return;
        }
        selectedDoctorId = (String) doctorTableModel.getValueAt(row, 0);
        List<TreatmentItem> treatments = api.getTreatments(selectedDoctorId);
        for (TreatmentItem treatment : treatments) {
            treatmentTableModel.addRow(new Object[]{treatment.getName(), treatment.getCost()});
        }
        styleTable(treatmentTable);
        updateTreatmentAreaState();
    }

    private void loadLogTable() {
        logTableModel.setRowCount(0);
        List<Appointment> appointments;
        if (staffFilterId != null) {
            appointments = api.getAppointmentsByStaff(staffFilterId);
        } else {
            appointments = api.searchAppointments(logSearchField.getText().trim());
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
        styleTable(logTable);
    }

    private void addStaff() {
        String[] values = showForm("Add Staff",
                new String[]{"Staff ID:", "Name:", "Username:", "Password:", "Role (Staff or Admin):"},
                new String[]{nextStaffId(), "", "", "", "Staff"});
        if (values == null) {
            return;
        }
        for (String value : values) {
            if (value.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Missing Details", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        api.addUser(new User(values[0], values[1], values[2], values[3], values[4]));
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
        api.editUser(new User(staffId, values[0], values[1], values[2], values[3]));
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
            api.removeUser(staffId);
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
        Doctor newDoctor = api.addDoctor(values[0], fee);
        loadDoctorTable();
        for (int i = 0; i < doctorTableModel.getRowCount(); i++) {
            if (doctorTableModel.getValueAt(i, 0).equals(newDoctor.getId())) {
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
        api.editConsultationFee(doctorId, fee);
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
        api.updateTreatments(selectedDoctorId, treatments);
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

    private double parseAmount(String input) {
        try {
            return Double.parseDouble(input.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String nextStaffId() {
        // finds the highest S number in the table and adds 1, e.g. S006 -> S007
        int highest = 0;
        for (User user : api.getAllUsers()) {
            try {
                int number = Integer.parseInt(user.getStaffId().substring(1));
                if (number > highest) {
                    highest = number;
                }
            } catch (NumberFormatException ex) {
                // ignore ids that do not follow the S001 pattern
            }
        }
        return "S" + String.format("%03d", highest + 1);
    }

    private String[] showForm(String title, String[] labels, String[] initialValues) {
        JPanel panel = new JPanel(new GridLayout(labels.length, 2, 12, 14));
        JComponent[] fields = new JComponent[labels.length];
        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i], JLabel.RIGHT);
            if (!labels[i].equals("Staff ID:")) {
                label.setText(label.getText() + " *");
            }
            UIStyles.styleFieldLabel(label);
            panel.add(label);
            if (labels[i].equals("Password:")) {
                JPasswordField password = new JPasswordField(initialValues[i]);
                UIStyles.styleField(password);
                fields[i] = password;
            } else if (labels[i].equals("Role (Staff or Admin):")) {
                JComboBox<String> combo = new JComboBox<>(new String[]{"Staff", "Admin"});
                combo.setSelectedItem(initialValues[i]);
                UIStyles.styleField(combo);
                fields[i] = combo;
            } else {
                JTextField text = new JTextField(initialValues[i]);
                UIStyles.styleField(text);
                if (labels[i].equals("Staff ID:")) {
                    // auto generated, so it is read only
                    text.setEditable(false);
                    text.setForeground(UIStyles.PLACEHOLDER);
                }
                fields[i] = text;
            }
            panel.add(fields[i]);
        }
        JOptionPane pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = pane.createDialog(this, title);
        dialog.setVisible(true);
        Object result = pane.getValue();
        if (!(result instanceof Integer) || (Integer) result != JOptionPane.OK_OPTION) {
            return null;
        }
        String[] values = new String[labels.length];
        for (int i = 0; i < labels.length; i++) {
            if (fields[i] instanceof JComboBox) {
                values[i] = (String) ((JComboBox<String>) fields[i]).getSelectedItem();
            } else if (fields[i] instanceof JPasswordField) {
                values[i] = new String(((JPasswordField) fields[i]).getPassword());
            } else {
                values[i] = ((JTextField) fields[i]).getText().trim();
            }
        }
        return values;
    }

    private void logout() {
        dispose();
        new LauncherFrame(api);
    }

    private void showHelp() {
        String help = "<html><body style='font-family:Segoe UI,sans-serif;font-size:13pt;color:#7A7A7A;'>"
                + "<div style='font-size:17pt;font-weight:bold;color:#7A7A7A;margin:0 0 14px 0;'>Admin help</div>"
                + "<div style='font-weight:bold;color:#7A7A7A;margin:0 0 4px 0;'>Staff Accounts tab:</div>"
                + "<ul style='margin-top:2px;'>"
                + "<li>View All Staff - refresh the staff list</li>"
                + "<li>Add Staff - create a new staff account (one form with all details)</li>"
                + "<li>Edit Staff / Remove Staff - manage existing accounts</li>"
                + "</ul>"
                + "<div style='background-color:#B9B9B9;height:1px;margin:10px 0 12px 0;'></div>"
                + "<div style='font-weight:bold;color:#7A7A7A;margin:0 0 4px 0;'>Doctors tab:</div>"
                + "<ul style='margin-top:2px;'>"
                + "<li>Add Doctor - add a doctor; their default treatment price list is filled in automatically</li>"
                + "<li>Edit Consultation Fee - change the doctor's consultation fee</li>"
                + "<li>Select a doctor, then edit their treatment prices in the table below:</li>"
                + "<li>Save Changes - saves the prices for the selected doctor</li>"
                + "<li>Add Row / Remove Row - add or delete treatments for that doctor</li>"
                + "</ul>"
                + "<div style='background-color:#B9B9B9;height:1px;margin:10px 0 12px 0;'></div>"
                + "<div style='font-weight:bold;color:#7A7A7A;margin:0 0 4px 0;'>Appointment Log tab:</div>"
                + "<ul style='margin-top:2px;'>"
                + "<li>Shows every appointment, its doctor, items total, and who handled it</li>"
                + "<li>Search - find by appointment ID, patient name, or a date (yyyy-MM-dd)</li>"
                + "<li>Click a staff member at the bottom to show only their appointments</li>"
                + "<li>Show All - clears the search and lists everything</li>"
                + "</ul>"
                + "<p style='margin-top:8px;'>Logout - return to the interface chooser.</p>"
                + "</body></html>";
        UIStyles.showHelpDialog(this, help);
    }
}
