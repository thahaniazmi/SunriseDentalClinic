package com.dental.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.dental.PinkButton;
import com.dental.UIStyles;
import com.dental.client.ApiClient;
import com.dental.model.Appointment;
import com.dental.model.Doctor;
import com.dental.model.TreatmentItem;
import com.dental.model.User;
import com.dental.service.BillingService;
import com.dental.util.ReceiptPrinter;

public class StaffFrame extends JFrame {
    private ApiClient api;
    private User loggedInUser;
    private BillingService billingService = new BillingService();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private JTextField patientNameField = new JTextField();
    private JTextField patientAddressField = new JTextField();
    private JTextField phoneField = new JTextField();
    private JTextField extraChargesField = new JTextField();
    private JComboBox<Doctor> doctorCombo = new JComboBox<>();
    private JTable itemsTable = new JTable();
    private DefaultTableModel itemsTableModel;
    private JSpinner dateSpinner;
    private JComboBox<String> timeCombo = new JComboBox<>();
    private String editingId;
    private JButton addButton;

    private JTextField searchField = new JTextField();
    private JTable resultsTable = new JTable();
    private DefaultTableModel resultsTableModel;
    private JTabbedPane tabs;
    private JPanel tableArea;
    private JTable homeTable = new JTable();
    private DefaultTableModel homeTableModel;
    private JTable upcomingTable = new JTable();
    private DefaultTableModel upcomingTableModel;

    public StaffFrame(ApiClient api, User loggedInUser) {
        this.api = api;
        this.loggedInUser = loggedInUser;

        setTitle("Sunrise Dental Clinic - Staff");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(990, 830);
        setMinimumSize(new Dimension(900, 700));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        UIStyles.styleField(patientNameField);
        UIStyles.styleField(patientAddressField);
        UIStyles.styleField(phoneField);
        UIStyles.styleField(extraChargesField);
        UIStyles.styleField(searchField);

        JLabel userLabel = new JLabel("Welcome, " + loggedInUser.getName());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userLabel.setForeground(UIStyles.TEXT_PRIMARY);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(10, 16, 10, 16));
        topPanel.add(userLabel, BorderLayout.WEST);

        loadDoctors();
        buildItemsTable();
        buildResultsTable();
        buildUpcomingTable();
        buildHomeTable();

        tabs = new JTabbedPane();
        tabs.addTab("Home", buildHomePanel());
        tabs.addTab("Book Appointment", buildBookPanel());
        tabs.addTab("Manage Appointments", buildManagePanel());

        JButton helpButton = new PinkButton("Help");
        helpButton.addActionListener(e -> showHelp());

        JButton logoutButton = new PinkButton("Logout");
        logoutButton.addActionListener(e -> logout());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.add(logoutButton);
        bottomPanel.add(helpButton);

        add(topPanel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private JPanel buildHomePanel() {
        // two tables stacked with a divider between them
        JLabel todayLabel = new JLabel("Today's Appointments (" + LocalDate.now() + "):");
        todayLabel.setFont(UIStyles.FONT_BOLD);
        todayLabel.setForeground(UIStyles.TEXT_PRIMARY);
        JScrollPane tableScroll = new JScrollPane(homeTable);
        tableScroll.setPreferredSize(new Dimension(940, 220));
        tableScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        tableScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel upcomingLabel = new JLabel("Upcoming Appointments (nearest first):");
        upcomingLabel.setFont(UIStyles.FONT_BOLD);
        upcomingLabel.setForeground(UIStyles.TEXT_PRIMARY);
        JScrollPane upcomingScroll = new JScrollPane(upcomingTable);
        upcomingScroll.setPreferredSize(new Dimension(940, 220));
        upcomingScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        upcomingScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel todayPanel = new JPanel(new BorderLayout());
        todayPanel.add(todayLabel, BorderLayout.NORTH);
        todayPanel.add(tableScroll, BorderLayout.CENTER);

        JPanel upcomingPanel = new JPanel(new BorderLayout());
        upcomingPanel.add(upcomingLabel, BorderLayout.NORTH);
        upcomingPanel.add(upcomingScroll, BorderLayout.CENTER);

        JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        tablesPanel.add(todayPanel);
        tablesPanel.add(upcomingPanel);

        JButton refreshButton = new PinkButton("Refresh");
        refreshButton.addActionListener(e -> refreshHomeTables());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(refreshButton);

        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBorder(new EmptyBorder(12, 16, 12, 16));
        homePanel.add(tablesPanel, BorderLayout.CENTER);
        homePanel.add(buttonPanel, BorderLayout.SOUTH);
        return homePanel;
    }

    private void refreshHomeTables() {
        // reload the tables after an update
        loadHomeTable();
    }

    private void applyItemsTableRenderers() {
        itemsTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JCheckBox box = new JCheckBox();
                box.setSelected(Boolean.TRUE.equals(value));
                box.setHorizontalAlignment(JCheckBox.CENTER);
                box.setVerticalAlignment(JCheckBox.CENTER);
                box.setOpaque(true);
                if (isSelected) {
                    box.setBackground(UIStyles.SELECTION);
                } else {
                    box.setBackground((row % 2 == 0) ? UIStyles.BACKGROUND : UIStyles.ALTERNATE_ROW);
                }
                return box;
            }
        });
        for (int i = 1; i < itemsTable.getColumnCount(); i++) {
            itemsTable.getColumnModel().getColumn(i).setCellRenderer(UIStyles.cellRenderer());
        }
    }

    private JPanel buildBookPanel() {
        dateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        UIStyles.styleField(dateSpinner);
        UIStyles.styleField(timeCombo);
        UIStyles.styleField(doctorCombo);

        for (String time : new String[]{"08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00"}) {
            timeCombo.addItem(time);
        }

        JLabel nameLabel = new JLabel("Patient name:");
        JLabel addressLabel = new JLabel("Patient address:");
        JLabel phoneLabel = new JLabel("Patient phone:");
        JLabel doctorLabel = new JLabel("Doctor:");
        JLabel dateLabel = new JLabel("Date:");
        JLabel timeLabel = new JLabel("Time:");
        JLabel chargesLabel = new JLabel("Additional charges (LKR):");
        UIStyles.styleFieldLabel(nameLabel);
        UIStyles.styleFieldLabel(addressLabel);
        UIStyles.styleFieldLabel(phoneLabel);
        UIStyles.styleFieldLabel(doctorLabel);
        UIStyles.styleFieldLabel(dateLabel);
        UIStyles.styleFieldLabel(timeLabel);
        UIStyles.styleFieldLabel(chargesLabel);

        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 12, 10));
        inputPanel.add(nameLabel);
        inputPanel.add(patientNameField);
        inputPanel.add(addressLabel);
        inputPanel.add(patientAddressField);
        inputPanel.add(phoneLabel);
        inputPanel.add(phoneField);
        inputPanel.add(doctorLabel);
        inputPanel.add(doctorCombo);
        inputPanel.add(dateLabel);
        inputPanel.add(dateSpinner);
        inputPanel.add(timeLabel);
        inputPanel.add(timeCombo);
        inputPanel.add(chargesLabel);
        inputPanel.add(extraChargesField);

        addButton = new PinkButton("Book Appointment");
        addButton.addActionListener(e -> saveAppointment());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(addButton);

        JLabel itemsLabel = new JLabel("Treatments (optional - tick any, amounts auto-fill, edit if needed):");
        UIStyles.styleFieldLabel(itemsLabel);
        JScrollPane itemsScroll = new JScrollPane(itemsTable);
        itemsScroll.setPreferredSize(new Dimension(940, 170));
        itemsScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));
        itemsScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBorder(new EmptyBorder(8, 0, 8, 0));
        itemsPanel.add(itemsLabel);
        itemsPanel.add(Box.createVerticalStrut(6));
        itemsPanel.add(itemsScroll);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(itemsPanel, BorderLayout.CENTER);

        JPanel bookPanel = new JPanel(new BorderLayout());
        bookPanel.setBorder(new EmptyBorder(12, 16, 12, 16));
        bookPanel.add(centerPanel, BorderLayout.CENTER);
        bookPanel.add(buttonPanel, BorderLayout.SOUTH);
        return bookPanel;
    }

    private JPanel buildManagePanel() {
        JLabel searchLabel = new JLabel("Search (ID, patient name or date yyyy-MM-dd):");
        UIStyles.styleFieldLabel(searchLabel);
        JPanel searchPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        searchPanel.setBorder(new EmptyBorder(12, 0, 12, 0));
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        JButton searchButton = new PinkButton("Search");
        searchButton.addActionListener(e -> searchAppointments());

        JButton viewButton = new PinkButton("View Details");
        viewButton.addActionListener(e -> viewDetails());

        JButton editButton = new PinkButton("Edit Appointment");
        editButton.addActionListener(e -> editAppointment());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(searchButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(editButton);

        JScrollPane tableScroll = new JScrollPane(resultsTable);
        tableScroll.setPreferredSize(new Dimension(940, 340));
        tableScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));
        tableScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.add(tableScroll, BorderLayout.CENTER);

        JLabel placeholderLabel = new JLabel("No appointments found", JLabel.CENTER);
        placeholderLabel.setForeground(UIStyles.PLACEHOLDER);
        placeholderLabel.setFont(placeholderLabel.getFont().deriveFont(Font.ITALIC));

        tableArea = new JPanel(new CardLayout());
        tableArea.add(placeholderLabel, "empty");
        tableArea.add(tableCard, "table");
        showAllAppointments();

        JPanel managePanel = new JPanel(new BorderLayout());
        managePanel.setBorder(new EmptyBorder(12, 16, 12, 16));
        managePanel.add(searchPanel, BorderLayout.NORTH);
        managePanel.add(tableArea, BorderLayout.CENTER);
        managePanel.add(buttonPanel, BorderLayout.SOUTH);
        return managePanel;
    }

    private void updateManageTableState() {
        if (tableArea == null) {
            return;
        }
        CardLayout layout = (CardLayout) tableArea.getLayout();
        layout.show(tableArea, resultsTableModel.getRowCount() == 0 ? "empty" : "table");
    }

    private void loadDoctors() {
        doctorCombo.removeAllItems();
        for (Doctor doctor : api.getDoctors()) {
            doctorCombo.addItem(doctor);
        }
        doctorCombo.addActionListener(e -> loadItemsForDoctor());
    }

    private void buildItemsTable() {
        itemsTableModel = new DefaultTableModel(new String[]{"Add", "Treatment", "Amount (LKR)"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 2;
            }
        };
        itemsTable.setModel(itemsTableModel);
        UIStyles.styleTable(itemsTable);
        itemsTable.setRowHeight(28); // the checkbox rows are a bit smaller
        applyItemsTableRenderers();
        loadItemsForDoctor();
    }

    private void loadItemsForDoctor() {
        itemsTableModel.setRowCount(0);
        Doctor selected = (Doctor) doctorCombo.getSelectedItem();
        if (selected == null) {
            return;
        }
        for (TreatmentItem item : api.getTreatments(selected.getId())) {
            itemsTableModel.addRow(new Object[]{Boolean.FALSE, item.getName(), item.getCost()});
        }
        styleTable(itemsTable);
    }

    private void buildResultsTable() {
        resultsTableModel = new DefaultTableModel(
                new String[]{"ID", "Patient", "Phone", "Doctor", "Date", "Time", "Staff ID", "Total (LKR)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultsTable.setModel(resultsTableModel);
        UIStyles.styleTable(resultsTable);
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

    private void buildHomeTable() {
        homeTableModel = new DefaultTableModel(
                new String[]{"ID", "Patient", "Phone", "Doctor", "Time", "Staff ID", "Total (LKR)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        homeTable.setModel(homeTableModel);
        UIStyles.styleTable(homeTable);
        loadHomeTable();
    }

    private void loadHomeTable() {
        homeTableModel.setRowCount(0);
        String today = LocalDate.now().toString();
        List<Appointment> appointments = api.getAppointmentsByDate(today, today);
        for (Appointment appointment : appointments) {
            homeTableModel.addRow(new Object[]{
                    appointment.getId(),
                    appointment.getPatientName(),
                    appointment.getPatientPhone(),
                    appointment.getDoctor(),
                    appointment.getTime(),
                    appointment.getHandledById(),
                    appointment.getTotal()
            });
        }
        styleTable(homeTable);
        loadUpcomingTable();
    }

    private void buildUpcomingTable() {
        upcomingTableModel = new DefaultTableModel(new String[]{"Appointment ID", "Patient Name", "Phone", "Doctor",
                "Date", "Time", "Staff ID", "Total (LKR)"}, 0);
        upcomingTable.setModel(upcomingTableModel);
        UIStyles.styleTable(upcomingTable);
        loadUpcomingTable();
    }

    private void loadUpcomingTable() {
        upcomingTableModel.setRowCount(0);
        String today = LocalDate.now().toString();
        List<Appointment> appointments = api.getUpcomingAppointments(today);
        for (Appointment appointment : appointments) {
            upcomingTableModel.addRow(new Object[]{
                    appointment.getId(),
                    appointment.getPatientName(),
                    appointment.getPatientPhone(),
                    appointment.getDoctor(),
                    appointment.getDate(),
                    appointment.getTime(),
                    appointment.getHandledById(),
                    appointment.getTotal()
            });
        }
        styleTable(upcomingTable);
    }

    private void saveAppointment() {
        String name = patientNameField.getText().trim();
        String phone = phoneField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the patient name.", "Missing Details", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String address = patientAddressField.getText().trim();
        if (address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the patient address.", "Missing Details", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the patient phone number.", "Missing Details", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Phone number must be exactly 10 digits.", "Invalid Phone", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (doctorCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a doctor.", "Missing Details", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Doctor doctor = (Doctor) doctorCombo.getSelectedItem();
        String date = dateFormat.format((Date) dateSpinner.getValue());
        String time = (String) timeCombo.getSelectedItem();

        List<TreatmentItem> selectedItems = new ArrayList<>();
        for (int i = 0; i < itemsTableModel.getRowCount(); i++) {
            boolean selected = (Boolean) itemsTableModel.getValueAt(i, 0);
            if (selected) {
                String itemName = String.valueOf(itemsTableModel.getValueAt(i, 1));
                String amountText = String.valueOf(itemsTableModel.getValueAt(i, 2));
                double amount = 0.0;
                try {
                    amount = Double.parseDouble(amountText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid amount for " + itemName + ".", "Invalid Amount", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                selectedItems.add(new TreatmentItem(itemName, amount));
            }
        }

        String extraText = extraChargesField.getText().trim();
        if (!extraText.isEmpty()) {
            double extraCharges = 0.0;
            try {
                extraCharges = Double.parseDouble(extraText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid additional charges amount.", "Invalid Amount", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (extraCharges > 0) {
                selectedItems.add(new TreatmentItem("Additional Charges", extraCharges));
            }
        }

        try {
            if (editingId != null) {
                Appointment updated = new Appointment(editingId, name, address, phone, doctor.toString(),
                        doctor.getConsultationFee(), selectedItems, date, time, loggedInUser.getName(), loggedInUser.getStaffId());
                api.updateAppointment(updated);
                JOptionPane.showMessageDialog(this, "Appointment " + editingId + " updated.", "Updated", JOptionPane.INFORMATION_MESSAGE);
                editingId = null;
                addButton.setText("Book Appointment");
                clearBookForm();
                tabs.setSelectedIndex(2);
            } else {
                Appointment saved = api.addAppointment(new Appointment(null, name, address, phone, doctor.toString(),
                        doctor.getConsultationFee(), selectedItems, date, time, loggedInUser.getName(), loggedInUser.getStaffId()));
                showReceipt("Receipt", "Appointment " + saved.getId() + " booked.\n\n"
                        + billingService.generateReceipt(saved));
                clearBookForm();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not save the appointment:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        loadHomeTable();
        searchAppointments();
    }

    private void showReceipt(String title, String text) {
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setRows(20);
        area.setColumns(44);
        int choice = JOptionPane.showOptionDialog(this, new JScrollPane(area), title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                new Object[]{"Print Receipt", "OK"}, "OK");
        if (choice == 0) {
            ReceiptPrinter.print(text);
        }
    }

    private void clearBookForm() {
        patientNameField.setText("");
        patientAddressField.setText("");
        phoneField.setText("");
        extraChargesField.setText("");
        doctorCombo.setSelectedIndex(0);
        dateSpinner.setValue(new Date());
        timeCombo.setSelectedIndex(0);
        for (int i = 0; i < itemsTableModel.getRowCount(); i++) {
            itemsTableModel.setValueAt(Boolean.FALSE, i, 0);
        }
    }

    private void searchAppointments() {
        resultsTableModel.setRowCount(0);
        List<Appointment> appointments = api.searchAppointments(searchField.getText().trim());
        for (Appointment appointment : appointments) {
            resultsTableModel.addRow(new Object[]{
                    appointment.getId(),
                    appointment.getPatientName(),
                    appointment.getPatientPhone(),
                    appointment.getDoctor(),
                    appointment.getDate(),
                    appointment.getTime(),
                    appointment.getHandledById(),
                    appointment.getTotal()
            });
        }
        styleTable(resultsTable);
        updateManageTableState();
    }

    private void showAllAppointments() {
        searchField.setText("");
        searchAppointments();
    }

    private void viewDetails() {
        int row = resultsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to view.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) resultsTableModel.getValueAt(row, 0);
        Appointment appointment = api.getAppointment(id);
        if (appointment == null) {
            JOptionPane.showMessageDialog(this, "Appointment not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        StringBuilder details = new StringBuilder();
        details.append("Appointment ID: ").append(appointment.getId()).append("\n");
        details.append("Patient: ").append(appointment.getPatientName()).append("\n");
        details.append("Address: ").append(appointment.getPatientAddress()).append("\n");
        details.append("Phone: ").append(appointment.getPatientPhone()).append("\n");
        details.append("Doctor: ").append(appointment.getDoctor()).append("\n");
        details.append("Date: ").append(appointment.getDate()).append("\n");
        details.append("Time: ").append(appointment.getTime()).append("\n");
        details.append("Handled by: ").append(appointment.getHandledBy()).append("\n\n");
        details.append(billingService.generateReceipt(appointment));
        showReceipt("Appointment Details", details.toString());
    }

    private void editAppointment() {
        int row = resultsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) resultsTableModel.getValueAt(row, 0);
        Appointment appointment = api.getAppointment(id);
        if (appointment == null) {
            JOptionPane.showMessageDialog(this, "Appointment not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        editingId = appointment.getId();
        patientNameField.setText(appointment.getPatientName());
        patientAddressField.setText(appointment.getPatientAddress());
        phoneField.setText(appointment.getPatientPhone());
        for (int i = 0; i < doctorCombo.getItemCount(); i++) {
            if (doctorCombo.getItemAt(i).toString().equals(appointment.getDoctor())) {
                doctorCombo.setSelectedIndex(i);
                break;
            }
        }
        loadItemsForDoctor();
        try {
            dateSpinner.setValue(dateFormat.parse(appointment.getDate()));
        } catch (Exception ex) {
            dateSpinner.setValue(new Date());
        }
        timeCombo.setSelectedItem(appointment.getTime());
        for (int i = 0; i < itemsTableModel.getRowCount(); i++) {
            boolean selected = false;
            for (TreatmentItem item : appointment.getItems()) {
                if (item.getName().equals(itemsTableModel.getValueAt(i, 1))) {
                    selected = true;
                    itemsTableModel.setValueAt(item.getCost(), i, 2);
                    break;
                }
            }
            itemsTableModel.setValueAt(selected, i, 0);
        }
        addButton.setText("Save Changes");
        tabs.setSelectedIndex(1);
    }

    private void showHelp() {
        String help = "<html><body style='font-family:Segoe UI,sans-serif;font-size:13pt;color:#7A7A7A;'>"
                + "<div style='font-size:17pt;font-weight:bold;color:#7A7A7A;margin:0 0 14px 0;'>Sunrise Dental Clinic - Staff Help</div>"
                + "<div style='font-weight:bold;color:#7A7A7A;margin:0 0 4px 0;'>Home tab:</div>"
                + "<ul style='margin-top:2px;'><li>shows today's appointments and upcoming ones. Press Refresh to update.</li></ul>"
                + "<div style='background-color:#B9B9B9;height:1px;margin:10px 0 12px 0;'></div>"
                + "<div style='font-weight:bold;color:#7A7A7A;margin:0 0 4px 0;'>Book Appointment tab:</div>"
                + "<ul style='margin-top:2px;'>"
                + "<li>Enter patient name and phone number.</li>"
                + "<li>Choose the doctor, date (click arrows or type yyyy-MM-dd) and time.</li>"
                + "<li>Treatments are optional: tick the ones the patient needs; prices come from that doctor's own price list.</li>"
                + "<li>Additional charges (e.g. medicines) can be typed in the extra field.</li>"
                + "<li>Press <b>'Book Appointment'</b> to save the appointment and show the receipt.</li>"
                + "<li>The appointment ID is assigned automatically.</li>"
                + "</ul>"
                + "<div style='background-color:#B9B9B9;height:1px;margin:10px 0 12px 0;'></div>"
                + "<div style='font-weight:bold;color:#7A7A7A;margin:0 0 4px 0;'>Manage Appointments tab:</div>"
                + "<ul style='margin-top:2px;'>"
                + "<li>Type in the search bar and press Search - works with appointment ID, patient name, or a date (yyyy-MM-dd).</li>"
                + "<li>View Details: shows the full receipt of the selected appointment.</li>"
                + "<li>Edit Appointment: loads the selected appointment into the Book tab; change details and press 'Save Changes'.</li>"
                + "</ul>"
                + "</body></html>";
        UIStyles.showHelpDialog(this, help);
    }

    private void logout() {
        new LauncherFrame(api).setVisible(true);
        dispose();
    }
}
