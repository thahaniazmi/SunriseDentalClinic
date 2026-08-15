package com.dental;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
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
import javax.swing.table.DefaultTableModel;

import com.dental.model.Appointment;
import com.dental.model.Doctor;
import com.dental.model.TreatmentItem;
import com.dental.model.User;
import com.dental.service.AppointmentService;
import com.dental.service.BillingService;
import com.dental.service.DoctorService;
import com.dental.service.DoctorTreatmentService;
import com.dental.service.UserService;
import com.dental.util.ReceiptPrinter;

public class StaffFrame extends JFrame {
    private AppointmentService appointmentService;
    private UserService userService;
    private DoctorService doctorService;
    private DoctorTreatmentService doctorTreatmentService;
    private User loggedInUser;
    private BillingService billingService = new BillingService();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private JTextField patientNameField = new JTextField();
    private JTextField phoneField = new JTextField();
    private JTextField extraChargesField = new JTextField();
    private JComboBox<Doctor> doctorCombo = new JComboBox<>();
    private JTable itemsTable = new JTable();
    private DefaultTableModel itemsTableModel;
    private JSpinner dateSpinner;
    private JComboBox<String> timeCombo = new JComboBox<>();
    private String editingId;
    private JButton addButton;
    private JButton deleteButton = new JButton("Delete Appointment");

    private JTextField searchField = new JTextField();
    private JTable resultsTable = new JTable();
    private DefaultTableModel resultsTableModel;
    private JTabbedPane tabs;
    private JTable homeTable = new JTable();
    private DefaultTableModel homeTableModel;
    private JTable upcomingTable = new JTable();
    private DefaultTableModel upcomingTableModel;

    public StaffFrame(AppointmentService appointmentService, UserService userService,
                      DoctorService doctorService, DoctorTreatmentService doctorTreatmentService,
                      User loggedInUser) {
        this.appointmentService = appointmentService;
        this.userService = userService;
        this.doctorService = doctorService;
        this.doctorTreatmentService = doctorTreatmentService;
        this.loggedInUser = loggedInUser;

        setTitle("Sunrise Dental Clinic - Staff");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel userLabel = new JLabel("Logged in as: " + loggedInUser.getName() + " (" + loggedInUser.getRole() + ")");

        loadDoctors();
        buildItemsTable();
        buildResultsTable();
        buildUpcomingTable();
        buildHomeTable();

        tabs = new JTabbedPane();
        tabs.addTab("Home", buildHomePanel());
        tabs.addTab("Book Appointment", buildBookPanel());
        tabs.addTab("Manage Appointments", buildManagePanel());

        add(userLabel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel buildHomePanel() {
        JLabel todayLabel = new JLabel("Today's Appointments (" + LocalDate.now() + "):");
        JScrollPane tableScroll = new JScrollPane(homeTable);
        tableScroll.setPreferredSize(new Dimension(660, 240));

        JLabel upcomingLabel = new JLabel("Upcoming Appointments:");
        JScrollPane upcomingScroll = new JScrollPane(upcomingTable);
        upcomingScroll.setPreferredSize(new Dimension(660, 240));

        JPanel tablesPanel = new JPanel(new GridLayout(2, 1));
        JPanel todayPanel = new JPanel(new BorderLayout());
        todayPanel.add(todayLabel, BorderLayout.NORTH);
        todayPanel.add(tableScroll, BorderLayout.CENTER);
        JPanel upcomingPanel = new JPanel(new BorderLayout());
        upcomingPanel.add(upcomingLabel, BorderLayout.NORTH);
        upcomingPanel.add(upcomingScroll, BorderLayout.CENTER);
        tablesPanel.add(todayPanel);
        tablesPanel.add(upcomingPanel);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadHomeTable());

        JButton helpButton = new JButton("Help");
        helpButton.addActionListener(e -> showHelp());

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        buttonPanel.add(refreshButton);
        buttonPanel.add(helpButton);
        buttonPanel.add(logoutButton);

        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.add(tablesPanel, BorderLayout.CENTER);
        homePanel.add(buttonPanel, BorderLayout.SOUTH);
        return homePanel;
    }

    private JPanel buildBookPanel() {
        dateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));

        for (String time : new String[]{"08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00"}) {
            timeCombo.addItem(time);
        }

        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        inputPanel.add(new JLabel("Patient name:"));
        inputPanel.add(patientNameField);
        inputPanel.add(new JLabel("Patient phone:"));
        inputPanel.add(phoneField);
        inputPanel.add(new JLabel("Doctor:"));
        inputPanel.add(doctorCombo);
        inputPanel.add(new JLabel("Date:"));
        inputPanel.add(dateSpinner);
        inputPanel.add(new JLabel("Time:"));
        inputPanel.add(timeCombo);
        inputPanel.add(new JLabel("Additional charges (LKR):"));
        inputPanel.add(extraChargesField);

        JLabel idLabel = new JLabel("Appointment ID will be assigned automatically.");
        idLabel.setHorizontalAlignment(JLabel.CENTER);

        addButton = new JButton("Add Appointment & Calculate Bill");
        addButton.addActionListener(e -> saveAppointment());

        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteAppointment());

        JButton helpButton = new JButton("Help");
        helpButton.addActionListener(e -> showHelp());

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4));
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(helpButton);
        buttonPanel.add(logoutButton);

        JLabel itemsLabel = new JLabel("Treatments (optional - tick any, amounts auto-fill, edit if needed):");
        JScrollPane itemsScroll = new JScrollPane(itemsTable);
        itemsScroll.setPreferredSize(new Dimension(660, 150));

        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.add(itemsLabel, BorderLayout.NORTH);
        itemsPanel.add(itemsScroll, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(itemsPanel, BorderLayout.CENTER);

        JPanel bookPanel = new JPanel(new BorderLayout());
        bookPanel.add(idLabel, BorderLayout.NORTH);
        bookPanel.add(centerPanel, BorderLayout.CENTER);
        bookPanel.add(buttonPanel, BorderLayout.SOUTH);
        return bookPanel;
    }

    private JPanel buildManagePanel() {
        JPanel searchPanel = new JPanel(new GridLayout(1, 2));
        searchPanel.add(new JLabel("Search (ID, patient name or date yyyy-MM-dd):"));
        searchPanel.add(searchField);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchAppointments());

        JButton showAllButton = new JButton("Show All");
        showAllButton.addActionListener(e -> showAllAppointments());

        JButton viewButton = new JButton("View Details");
        viewButton.addActionListener(e -> viewDetails());

        JButton editButton = new JButton("Edit Appointment");
        editButton.addActionListener(e -> editAppointment());

        JButton helpButton = new JButton("Help");
        helpButton.addActionListener(e -> showHelp());

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 6));
        buttonPanel.add(searchButton);
        buttonPanel.add(showAllButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(editButton);
        buttonPanel.add(helpButton);
        buttonPanel.add(logoutButton);

        JScrollPane tableScroll = new JScrollPane(resultsTable);

        JPanel managePanel = new JPanel(new BorderLayout());
        managePanel.add(searchPanel, BorderLayout.NORTH);
        managePanel.add(tableScroll, BorderLayout.CENTER);
        managePanel.add(buttonPanel, BorderLayout.SOUTH);
        return managePanel;
    }

    private void loadDoctors() {
        doctorCombo.removeAllItems();
        for (Doctor doctor : doctorService.getAllDoctors()) {
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
        loadItemsForDoctor();
    }

    private void loadItemsForDoctor() {
        itemsTableModel.setRowCount(0);
        Doctor selected = (Doctor) doctorCombo.getSelectedItem();
        if (selected == null) {
            return;
        }
        for (TreatmentItem item : doctorTreatmentService.getAllForDoctor(selected.getId())) {
            itemsTableModel.addRow(new Object[]{Boolean.FALSE, item.getName(), item.getCost()});
        }
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
        loadHomeTable();
    }

    private void loadHomeTable() {
        homeTableModel.setRowCount(0);
        String today = LocalDate.now().toString();
        List<Appointment> appointments = appointmentService.searchByDate(today, today);
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
        loadUpcomingTable();
    }

    private void buildUpcomingTable() {
        upcomingTableModel = new DefaultTableModel(new String[]{"Appointment ID", "Patient Name", "Phone", "Doctor",
                "Date", "Time", "Staff ID", "Total (LKR)"}, 0);
        upcomingTable.setModel(upcomingTableModel);
        loadUpcomingTable();
    }

    private void loadUpcomingTable() {
        upcomingTableModel.setRowCount(0);
        String today = LocalDate.now().toString();
        List<Appointment> appointments = appointmentService.searchUpcoming(today);
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
    }

    private void saveAppointment() {
        String name = patientNameField.getText().trim();
        String phone = phoneField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the patient name.", "Missing Details", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the patient phone number.", "Missing Details", JOptionPane.WARNING_MESSAGE);
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
                Appointment updated = new Appointment(editingId, name, phone, doctor.toString(), doctor.getConsultationFee(),
                        selectedItems, date, time, loggedInUser.getName(), loggedInUser.getStaffId());
                appointmentService.updateAppointment(updated);
                JOptionPane.showMessageDialog(this, "Appointment " + editingId + " updated.", "Updated", JOptionPane.INFORMATION_MESSAGE);
                editingId = null;
                addButton.setText("Add Appointment & Calculate Bill");
                clearBookForm();
            } else {
                Appointment saved = appointmentService.addAppointment(new Appointment(null, name, phone, doctor.toString(),
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
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
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
        phoneField.setText("");
        extraChargesField.setText("");
        doctorCombo.setSelectedIndex(0);
        dateSpinner.setValue(new Date());
        timeCombo.setSelectedIndex(0);
        deleteButton.setEnabled(false);
        for (int i = 0; i < itemsTableModel.getRowCount(); i++) {
            itemsTableModel.setValueAt(Boolean.FALSE, i, 0);
        }
    }

    private void searchAppointments() {
        resultsTableModel.setRowCount(0);
        List<Appointment> appointments = appointmentService.search(searchField.getText().trim());
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
        Appointment appointment = appointmentService.findById(id);
        if (appointment == null) {
            JOptionPane.showMessageDialog(this, "Appointment not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        StringBuilder details = new StringBuilder();
        details.append("Appointment ID: ").append(appointment.getId()).append("\n");
        details.append("Patient: ").append(appointment.getPatientName()).append("\n");
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
        Appointment appointment = appointmentService.findById(id);
        if (appointment == null) {
            JOptionPane.showMessageDialog(this, "Appointment not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        editingId = appointment.getId();
        patientNameField.setText(appointment.getPatientName());
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
        deleteButton.setEnabled(true);
        tabs.setSelectedIndex(1);
        JOptionPane.showMessageDialog(this, "Editing appointment " + id + ". Change the details and press 'Save Changes'.", "Edit Appointment", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteAppointment() {
        if (editingId == null) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete appointment " + editingId + "? This will remove it from the system.", "Delete Appointment", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            appointmentService.deleteAppointment(editingId);
            JOptionPane.showMessageDialog(this, "Appointment " + editingId + " deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
            editingId = null;
            addButton.setText("Add Appointment & Calculate Bill");
            clearBookForm();
            loadHomeTable();
            searchAppointments();
        }
    }

    private void showHelp() {
        String help = "Sunrise Dental Clinic - Staff Help\n\n"
                + "Home tab: shows today's appointments and upcoming ones. Press Refresh to update.\n\n"
                + "Book Appointment tab:\n"
                + "  - Enter patient name and phone number.\n"
                + "  - Choose the doctor, date (click arrows or type yyyy-MM-dd) and time.\n"
                + "  - Treatments are optional: tick the ones the patient needs; prices come from that doctor's own price list.\n"
                + "  - Additional charges (e.g. medicines) can be typed in the extra field.\n"
                + "  - Press 'Add Appointment & Calculate Bill' to save the appointment and show the receipt.\n"
                + "  - The appointment ID is assigned automatically.\n\n"
                + "Manage Appointments tab:\n"
                + "  - Type in the search bar and press Search - works with appointment ID, patient name, or a date (yyyy-MM-dd).\n"
                + "  - Show All - clears the search and lists every appointment.\n"
                + "  - View Details: shows the full receipt of the selected appointment.\n"
                + "  - Edit Appointment: loads the selected appointment into the Book tab; change details and press 'Save Changes'.\n"
                + "  - While editing, 'Delete Appointment' removes that appointment from the system.";
        JOptionPane.showMessageDialog(this, help, "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        new LauncherFrame(userService, appointmentService, doctorService, doctorTreatmentService).setVisible(true);
        dispose();
    }
}