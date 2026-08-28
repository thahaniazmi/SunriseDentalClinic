package com.dental.service;

import java.time.LocalDate;
import java.util.List;

import com.dental.dao.AppointmentDAO;
import com.dental.model.Appointment;
import com.dental.model.TreatmentItem;

public class AppointmentServiceImpl implements AppointmentService {
    private AppointmentDAO dao = new AppointmentDAO();

    public AppointmentServiceImpl() {
        seedIfEmpty();
        seedYearlyDummies();
        dao.remapOldStaffToCurrentAccounts();
    }

    private void seedIfEmpty() {
        // sample appointments on different days so the lists are not empty
        if (dao.count() > 0) {
            return;
        }
        LocalDate today = LocalDate.now();
        String yesterday = today.minusDays(1).toString();
        String twoDaysAgo = today.minusDays(2).toString();
        String lastWeek = today.minusDays(7).toString();
        String tomorrow = today.plusDays(1).toString();
        String inTwoDays = today.plusDays(2).toString();
        String inThreeDays = today.plusDays(3).toString();
        String nextWeek = today.plusDays(7).toString();
        String threeWeeksAgo = today.minusDays(21).toString();
        String fiveWeeksAgo = today.minusDays(35).toString();
        String sixDaysAgo = today.minusDays(6).toString();
        String fourDaysAgo = today.minusDays(4).toString();
        String threeDaysAgo = today.minusDays(3).toString();
        String inFourDays = today.plusDays(4).toString();
        String inFiveDays = today.plusDays(5).toString();
        String inSixDays = today.plusDays(6).toString();
        String inTwoWeeks = today.plusDays(14).toString();

        Appointment a1 = new Appointment("A001", "Kavindu Perera", "No. 12, Galle Road, Dehiwala", "0712345678", "Dr. Anna", 500.0,
                List.of(new TreatmentItem("Cleaning", 1500.0)),
                lastWeek, "09:00", "Admin", "S001");
        Appointment a2 = new Appointment("A002", "Nimal Fernando", "No. 45/3, Kandy Road, Kadawatha", "0723456789", "Dr. Malik", 700.0,
                List.of(new TreatmentItem("Filling", 3250.0)),
                lastWeek, "10:30", "User", "S002");
        Appointment a3 = new Appointment("A003", "Sachini Silva", "No. 8, Temple Lane, Nugegoda", "0734567890", "Ms. Nisa", 450.0,
                List.of(new TreatmentItem("Whitening", 4200.0),
                        new TreatmentItem("Fluoride Treatment", 1100.0)),
                twoDaysAgo, "11:00", "Admin", "S001");
        Appointment a4 = new Appointment("A004", "Tharindu Weerasinghe", "No. 101, Negombo Road, Ja-Ela", "0745678901", "Dr. Perera", 800.0,
                List.of(new TreatmentItem("Root Canal", 14000.0)),
                yesterday, "09:30", "Admin", "S001");
        Appointment a5 = new Appointment("A005", "Amaya Jayasuriya", "No. 23, High Level Road, Maharagama", "0756789012", "Dr. Fernando", 600.0,
                List.of(new TreatmentItem("Extraction", 2900.0),
                        new TreatmentItem("Cleaning", 1600.0)),
                yesterday, "14:00", "User", "S002");
        Appointment a6 = new Appointment("A006", "Isuru Bandara", "No. 67, Baseline Road, Colombo 09", "0767890123", "Dr. Anna", 500.0,
                List.of(new TreatmentItem("Scaling", 3500.0)),
                today.toString(), "09:00", "Admin", "S001");
        Appointment a7 = new Appointment("A007", "Kasun Wickramasinghe", "No. 5/2, Horana Road, Panadura", "0778901234", "Dr. Malik", 700.0,
                List.of(new TreatmentItem("Cleaning", 1750.0),
                        new TreatmentItem("Dental X-Ray", 2250.0)),
                today.toString(), "10:00", "User", "S002");
        Appointment a8 = new Appointment("A008", "Nadeesha Gunasekara", "No. 89, Duplication Road, Colombo 03", "0789012345", "Ms. Nisa", 450.0,
                List.of(new TreatmentItem("Cleaning", 1400.0),
                        new TreatmentItem("Fluoride Treatment", 1100.0)),
                today.toString(), "11:30", "Admin", "S001");
        Appointment a9 = new Appointment("A009", "Ruwan Dias", "No. 14, Mount Avenue, Mount Lavinia", "0790123456", "Dr. Perera", 800.0,
                List.of(new TreatmentItem("Dental X-Ray", 2500.0),
                        new TreatmentItem("Extraction", 3300.0)),
                today.toString(), "14:00", "User", "S002");
        Appointment a10 = new Appointment("A010", "Dilani Perera", "No. 32, Wattala Road, Hendala", "0701234567", "Dr. Fernando", 600.0,
                List.of(new TreatmentItem("Whitening", 4300.0),
                        new TreatmentItem("Filling", 3400.0)),
                today.toString(), "15:30", "Admin", "S001");
        Appointment a11 = new Appointment("A011", "Sahan Jayawardena", "No. 76, Old Kesbewa Road, Boralesgamuwa", "0719876543", "Dr. Anna", 500.0,
                List.of(new TreatmentItem("Filling", 3000.0),
                        new TreatmentItem("Fluoride Treatment", 1000.0)),
                tomorrow, "08:30", "User", "S002");
        Appointment a12 = new Appointment("A012", "Thilini Rathnayake", "No. 9, Malabe Road, Thalangama", "0775551234", "Dr. Malik", 700.0,
                List.of(new TreatmentItem("Root Canal", 12500.0)),
                tomorrow, "10:00", "Admin", "S001");
        Appointment a13 = new Appointment("A013", "Chamari Atapattu", "No. 58, Moratuwa Road, Piliyandala", "0764447890", "Ms. Nisa", 450.0,
                List.of(new TreatmentItem("Whitening", 4200.0)),
                tomorrow, "13:30", "User", "S002");
        Appointment a14 = new Appointment("A014", "Dasun Liyanage", "No. 120, Gampaha Road, Kiribathgoda", "0753332109", "Dr. Fernando", 600.0,
                List.of(new TreatmentItem("Braces", 46000.0)),
                inTwoDays, "09:00", "Admin", "S001");
        Appointment a15 = new Appointment("A015", "Ishara Madushani", "No. 3, Station Road, Ragama", "0742228765", "Dr. Perera", 800.0,
                List.of(new TreatmentItem("Cleaning", 2100.0),
                        new TreatmentItem("Dental X-Ray", 2500.0)),
                inTwoDays, "11:00", "User", "S002");
        Appointment a16 = new Appointment("A016", "Pasindu Jayawardena", "No. 41, Angulana Road, Lunawa", "0781113456", "Dr. Anna", 500.0,
                List.of(new TreatmentItem("Whitening", 4500.0)),
                inThreeDays, "10:30", "Admin", "S001");
        Appointment a17 = new Appointment("A017", "Kaushalya Fernando", "No. 27, Sea Street, Kotahena", "0712345679", "Dr. Malik", 700.0,
                List.of(new TreatmentItem("Extraction", 2750.0),
                        new TreatmentItem("Filling", 3250.0)),
                nextWeek, "14:30", "User", "S002");

        addAppointment(a1);
        addAppointment(a2);
        addAppointment(a3);
        addAppointment(a4);
        addAppointment(a5);
        addAppointment(a6);
        addAppointment(a7);
        addAppointment(a8);
        addAppointment(a9);
        addAppointment(a10);
        addAppointment(a11);
        addAppointment(a12);
        addAppointment(a13);
        addAppointment(a14);
        addAppointment(a15);
        addAppointment(a16);
        addAppointment(a17);

        // some older and further-out bookings
        addAppointment(new Appointment("A018", "Nuwan Rathnayake", "No. 66, Peradeniya Road, Kandy", "0771230001", "Dr. Gomes", 750.0,
                List.of(new TreatmentItem("Dental X-Ray", 2200.0)), fiveWeeksAgo, "09:00", "Admin", "S001"));
        addAppointment(new Appointment("A019", "Oshini Dissanayake", "No. 18, Nawala Road, Rajagiriya", "0771230002", "Dr. Anna", 500.0,
                List.of(new TreatmentItem("Cleaning", 1500.0), new TreatmentItem("Scaling", 3500.0)), threeWeeksAgo, "10:30", "User", "S002"));
        addAppointment(new Appointment("A020", "Hasitha Gunawardena", "No. 55, Homagama Road, Kottawa", "0771230003", "Dr. Malik", 700.0,
                List.of(new TreatmentItem("Root Canal", 13000.0)), threeWeeksAgo, "14:00", "Admin", "S001"));
        addAppointment(new Appointment("A021", "Sanduni Karunaratne", "No. 7, Kohuwala Junction, Dehiwala", "0771230004", "Ms. Nisa", 450.0,
                List.of(new TreatmentItem("Whitening", 4200.0)), sixDaysAgo, "09:30", "User", "S002"));
        addAppointment(new Appointment("A022", "Tharaka Peiris", "No. 99, Avissawella Road, Welikada", "0771230005", "Dr. Fernando", 600.0,
                List.of(new TreatmentItem("Extraction", 3000.0)), fourDaysAgo, "11:00", "User", "S002"));
        addAppointment(new Appointment("A023", "Madhusha Wijesinghe", "No. 31, Kesbewa Road, Pita Kotte", "0771230006", "Dr. Gomes", 750.0,
                List.of(new TreatmentItem("Cleaning", 1900.0), new TreatmentItem("Fluoride Treatment", 1100.0)), threeDaysAgo, "15:00", "Admin", "S001"));
        addAppointment(new Appointment("A024", "Dilshan Abeysinghe", "No. 72, Main Street, Boralesgamuwa", "0771230007", "Dr. Anna", 500.0,
                List.of(new TreatmentItem("Filling", 3100.0)), inFourDays, "08:30", "Admin", "S001"));
        addAppointment(new Appointment("A025", "Nipuni Herath", "No. 16, Kaduwela Road, Malabe", "0771230008", "Dr. Perera", 800.0,
                List.of(new TreatmentItem("Braces", 48000.0)), inFiveDays, "10:00", "User", "S002"));
        addAppointment(new Appointment("A026", "Charith Senanayake", "No. 84, Pannipitiya Road, Thalahena", "0771230009", "Dr. Malik", 700.0,
                List.of(new TreatmentItem("Cleaning", 1750.0), new TreatmentItem("Dental X-Ray", 2250.0)), inSixDays, "13:30", "User", "S002"));
        addAppointment(new Appointment("A027", "Yasas Fernando", "No. 50, Rawathawatta Road, Moratuwa", "0771230010", "Dr. Gomes", 750.0,
                List.of(new TreatmentItem("Whitening", 4500.0)), inTwoWeeks, "14:30", "Admin", "S001"));
    }

    @Override
    public Appointment addAppointment(Appointment appointment) {
        if (appointment.getPatientName() == null || appointment.getPatientName().trim().isEmpty()) {
            throw new IllegalArgumentException("Patient name is required");
        }
        if (appointment.getPatientPhone() == null || !appointment.getPatientPhone().matches("\\d{10}")) {
            throw new IllegalArgumentException("Phone number must be exactly 10 digits");
        }
        return dao.add(appointment);
    }

    @Override
    public List<Appointment> viewAppointments() {
        return dao.findAll();
    }

    @Override
    public Appointment findById(String id) {
        return dao.findById(id);
    }

    @Override
    public List<Appointment> searchByPatientName(String patientName) {
        return dao.findByPatientName(patientName);
    }

    @Override
    public List<Appointment> searchByDate(String fromDate, String toDate) {
        return dao.findByDateRange(fromDate, toDate);
    }

    @Override
    public List<Appointment> searchUpcoming(String fromDate) {
        return dao.findUpcoming(fromDate);
    }

    @Override
    public List<Appointment> searchByStaffId(String staffId) {
        return dao.findByStaffId(staffId);
    }

    @Override
    public List<Appointment> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return viewAppointments();
        }
        String q = query.trim();
        Appointment byId = findById(q);
        if (byId != null) {
            return List.of(byId);
        }
        if (q.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return searchByDate(q, q);
        }
        return searchByPatientName(q);
    }

    @Override
    public void updateAppointment(Appointment appointment) {
        dao.update(appointment);
    }

    @Override
    public int countAppointments(String fromDate, String toDate, String dentistName) {
        return dao.count(fromDate, toDate, dentistName);
    }

    // extra dummy bookings spread over last year and this year for every dentist
    private void seedYearlyDummies() {
        addIfMissing(new Appointment("A028", "Sanduni Alwis", "No. 21, Temple Road, Kohuwala", "0771230011", "Dr. Anna", 500.0,
                List.of(new TreatmentItem("Cleaning", 1500.0)), "2025-01-16", "09:00", "Admin", "S001"));
        addIfMissing(new Appointment("A029", "Ruwan Jayalath", "No. 33, Castle Street, Colombo 15", "0771230012", "Dr. Anna", 500.0,
                List.of(new TreatmentItem("Filling", 3100.0)), "2025-04-09", "10:30", "User", "S002"));
        addIfMissing(new Appointment("A030", "Menaka Weerasinghe", "No. 4, Station Road, Dehiwala", "0771230013", "Dr. Anna", 500.0,
                List.of(new TreatmentItem("Whitening", 4200.0)), "2025-07-22", "13:00", "Admin", "S001"));
        addIfMissing(new Appointment("A031", "Kasun Alahakoon", "No. 77, Negombo Road, Wattala", "0771230014", "Dr. Anna", 500.0,
                List.of(new TreatmentItem("Scaling", 3400.0)), "2025-10-11", "11:00", "User", "S002"));
        addIfMissing(new Appointment("A032", "Tharindi Gunasekara", "No. 12, Lake Road, Boralesgamuwa", "0771230015", "Dr. Anna", 500.0,
                List.of(new TreatmentItem("Fluoride Treatment", 1100.0)), "2026-01-20", "09:30", "Admin", "S001"));
        addIfMissing(new Appointment("A033", "Ashan Perera", "No. 58, High Street, Kandy", "0771230016", "Dr. Anna", 500.0,
                List.of(new TreatmentItem("Cleaning", 1600.0)), "2026-03-14", "14:00", "User", "S002"));
        addIfMissing(new Appointment("A034", "Dinithi Samaraweera", "No. 9, Galle Face Terrace, Colombo 03", "0771230017", "Dr. Anna", 500.0,
                List.of(new TreatmentItem("Dental X-Ray", 2300.0)), "2026-05-06", "10:00", "Admin", "S001"));
        addIfMissing(new Appointment("A035", "Sahan Wickramaratne", "No. 45, Baseline Road, Kirulapone", "0771230018", "Dr. Anna", 500.0,
                List.of(new TreatmentItem("Filling", 3250.0)), "2026-07-08", "15:30", "User", "S002"));

        addIfMissing(new Appointment("A036", "Nimesha Rajapaksa", "No. 17, Ward Place, Colombo 07", "0771230019", "Dr. Malik", 700.0,
                List.of(new TreatmentItem("Root Canal", 13000.0)), "2025-02-13", "09:00", "User", "S002"));
        addIfMissing(new Appointment("A037", "Chamika Silva", "No. 62, Havelock Road, Colombo 05", "0771230020", "Dr. Malik", 700.0,
                List.of(new TreatmentItem("Extraction", 2900.0)), "2025-05-21", "11:30", "Admin", "S001"));
        addIfMissing(new Appointment("A038", "Ishan Fernando", "No. 28, Sea Avenue, Mount Lavinia", "0771230021", "Dr. Malik", 700.0,
                List.of(new TreatmentItem("Filling", 3350.0)), "2025-08-06", "14:30", "User", "S002"));
        addIfMissing(new Appointment("A039", "Amaya Bandara", "No. 5, Circular Road, Nugegoda", "0771230022", "Dr. Malik", 700.0,
                List.of(new TreatmentItem("Cleaning", 1750.0)), "2025-11-18", "16:00", "Admin", "S001"));
        addIfMissing(new Appointment("A040", "Rukshan Dias", "No. 90, Kaduwela Road, Battaramulla", "0771230023", "Dr. Malik", 700.0,
                List.of(new TreatmentItem("Root Canal", 12500.0)), "2026-01-29", "08:30", "User", "S002"));
        addIfMissing(new Appointment("A041", "Hasini Gunawardena", "No. 36, Thimbirigasyaya Road, Colombo 06", "0771230024", "Dr. Malik", 700.0,
                List.of(new TreatmentItem("Scaling", 3500.0)), "2026-03-25", "12:00", "Admin", "S001"));
        addIfMissing(new Appointment("A042", "Thilina Perera", "No. 71, Panchikawatta Road, Colombo 10", "0771230025", "Dr. Malik", 700.0,
                List.of(new TreatmentItem("Dental X-Ray", 2250.0)), "2026-05-19", "13:30", "User", "S002"));
        addIfMissing(new Appointment("A043", "Shenali de Silva", "No. 15, Marine Drive, Wellawatte", "0771230026", "Dr. Malik", 700.0,
                List.of(new TreatmentItem("Extraction", 3050.0)), "2026-07-15", "15:00", "Admin", "S001"));

        addIfMissing(new Appointment("A044", "Gayani Kumari", "No. 8, Pagoda Road, Nugegoda", "0771230027", "Ms. Nisa", 450.0,
                List.of(new TreatmentItem("Whitening", 4300.0)), "2025-01-28", "09:30", "Admin", "S001"));
        addIfMissing(new Appointment("A045", "Suresh Peiris", "No. 49, Keselwatte Lane, Kolonnawa", "0771230028", "Ms. Nisa", 450.0,
                List.of(new TreatmentItem("Fluoride Treatment", 1050.0)), "2025-04-30", "10:00", "User", "S002"));
        addIfMissing(new Appointment("A046", "Niluka Pathirana", "No. 26, Nawala Road, Nawala", "0771230029", "Ms. Nisa", 450.0,
                List.of(new TreatmentItem("Cleaning", 1450.0)), "2025-07-09", "11:45", "Admin", "S001"));
        addIfMissing(new Appointment("A047", "Dinesh Herath", "No. 83, Kandy Road, Kadawatha", "0771230030", "Ms. Nisa", 450.0,
                List.of(new TreatmentItem("Whitening", 4100.0)), "2025-10-23", "14:15", "User", "S002"));
        addIfMissing(new Appointment("A048", "Kavisha Ilapperuma", "No. 2, Sunethra Lane, Pepiliyana", "0771230031", "Ms. Nisa", 450.0,
                List.of(new TreatmentItem("Fluoride Treatment", 1150.0)), "2026-02-05", "09:00", "Admin", "S001"));
        addIfMissing(new Appointment("A049", "Buddhika Wijetunga", "No. 57, Horana Road, Kesbewa", "0771230032", "Ms. Nisa", 450.0,
                List.of(new TreatmentItem("Cleaning", 1550.0)), "2026-04-14", "10:45", "User", "S002"));
        addIfMissing(new Appointment("A050", "Rashmi Senanayake", "No. 39, Elvitigala Mawatha, Colombo 05", "0771230033", "Ms. Nisa", 450.0,
                List.of(new TreatmentItem("Scaling", 3300.0)), "2026-06-03", "13:15", "Admin", "S001"));
        addIfMissing(new Appointment("A051", "Malith Fonseka", "No. 64, Dutugemunu Street, Kohuwala", "0771230034", "Ms. Nisa", 450.0,
                List.of(new TreatmentItem("Whitening", 4400.0)), "2026-07-27", "16:30", "User", "S002"));

        addIfMissing(new Appointment("A052", "Pradeep Munasinghe", "No. 30, Madapatha Road, Homagama", "0771230035", "Dr. Perera", 800.0,
                List.of(new TreatmentItem("Braces", 47000.0)), "2025-03-06", "09:00", "User", "S002"));
        addIfMissing(new Appointment("A053", "Anusha Ekanayake", "No. 11, Kurunegala Road, Divulapitiya", "0771230036", "Dr. Perera", 800.0,
                List.of(new TreatmentItem("Root Canal", 13500.0)), "2025-06-17", "10:15", "Admin", "S001"));
        addIfMissing(new Appointment("A054", "Lahiru Madushanka", "No. 88, Minuwangoda Road, Ganemulla", "0771230037", "Dr. Perera", 800.0,
                List.of(new TreatmentItem("Extraction", 3150.0)), "2025-09-25", "12:30", "User", "S002"));
        addIfMissing(new Appointment("A055", "Sewwandi Perera", "No. 7, Vihara Road, Kelaniya", "0771230038", "Dr. Perera", 800.0,
                List.of(new TreatmentItem("Cleaning", 1800.0)), "2025-12-10", "15:45", "Admin", "S001"));
        addIfMissing(new Appointment("A056", "Nuwan Gamage", "No. 52, Biyagama Road, Wanaluwawa", "0771230039", "Dr. Perera", 800.0,
                List.of(new TreatmentItem("Dental X-Ray", 2400.0)), "2026-01-08", "09:45", "User", "S002"));
        addIfMissing(new Appointment("A057", "Chathura Weerarathne", "No. 19, Godagama Road, Meegoda", "0771230040", "Dr. Perera", 800.0,
                List.of(new TreatmentItem("Braces", 45500.0)), "2026-04-02", "11:15", "Admin", "S001"));
        addIfMissing(new Appointment("A058", "Dilini Abeysekara", "No. 43, Hokandara Road, Thalangama", "0771230041", "Dr. Perera", 800.0,
                List.of(new TreatmentItem("Filling", 3450.0)), "2026-06-18", "14:45", "User", "S002"));
        addIfMissing(new Appointment("A059", "Randil Cooray", "No. 25, Ja-Ela Road, Ekala", "0771230042", "Dr. Perera", 800.0,
                List.of(new TreatmentItem("Scaling", 3600.0)), "2026-07-30", "16:15", "Admin", "S001"));

        addIfMissing(new Appointment("A060", "Wimal Sirisena", "No. 60, Ratnapura Road, Kahathuduwa", "0771230043", "Dr. Fernando", 600.0,
                List.of(new TreatmentItem("Filling", 3200.0)), "2025-02-04", "09:15", "Admin", "S001"));
        addIfMissing(new Appointment("A061", "Chandima Alwis", "No. 34, Maharagama Road, Pannipitiya", "0771230044", "Dr. Fernando", 600.0,
                List.of(new TreatmentItem("Cleaning", 1700.0)), "2025-05-08", "10:45", "User", "S002"));
        addIfMissing(new Appointment("A062", "Tania Rodrigo", "No. 6, De Saram Road, Mount Lavinia", "0771230045", "Dr. Fernando", 600.0,
                List.of(new TreatmentItem("Extraction", 2800.0)), "2025-08-19", "13:45", "Admin", "S001"));
        addIfMissing(new Appointment("A063", "Sanjeewa Karunathilaka", "No. 95, Old Road, Moratuwa", "0771230046", "Dr. Fernando", 600.0,
                List.of(new TreatmentItem("Whitening", 4250.0)), "2025-11-27", "15:15", "User", "S002"));
        addIfMissing(new Appointment("A064", "Oshadi Ranasinghe", "No. 48, Polhengoda Road, Colombo 06", "0771230047", "Dr. Fernando", 600.0,
                List.of(new TreatmentItem("Filling", 3300.0)), "2026-01-15", "08:45", "Admin", "S001"));
        addIfMissing(new Appointment("A065", "Akila Jayanetti", "No. 13, Araliya Place, Kalubowila", "0771230048", "Dr. Fernando", 600.0,
                List.of(new TreatmentItem("Cleaning", 1850.0)), "2026-03-31", "11:45", "User", "S002"));
        addIfMissing(new Appointment("A066", "Menik Hettiarachchi", "No. 80, Kawdana Road, Dehiwala", "0771230049", "Dr. Fernando", 600.0,
                List.of(new TreatmentItem("Scaling", 3450.0)), "2026-06-10", "14:15", "Admin", "S001"));
        addIfMissing(new Appointment("A067", "Pasan Kumarasiri", "No. 22, Gorakana Road, Kesbewa", "0771230050", "Dr. Fernando", 600.0,
                List.of(new TreatmentItem("Fluoride Treatment", 1200.0)), "2026-07-21", "16:45", "User", "S002"));

        addIfMissing(new Appointment("A068", "Rohan Dissanayake", "No. 38, Wattegedara Road, Maharagama", "0771230051", "Dr. Gomes", 750.0,
                List.of(new TreatmentItem("Whitening", 4500.0)), "2025-03-19", "09:45", "User", "S002"));
        addIfMissing(new Appointment("A069", "Priyanka Mendis", "No. 54, Rawatawatte Road, Moratuwa", "0771230052", "Dr. Gomes", 750.0,
                List.of(new TreatmentItem("Root Canal", 12800.0)), "2025-06-25", "11:15", "Admin", "S001"));
        addIfMissing(new Appointment("A070", "Ajith Balasuriya", "No. 29, Lunawa Road, Lunawa", "0771230053", "Dr. Gomes", 750.0,
                List.of(new TreatmentItem("Cleaning", 1650.0)), "2025-09-11", "13:45", "User", "S002"));
        addIfMissing(new Appointment("A071", "Nayana Wickramasuriya", "No. 75, Galle Road, Ratmalana", "0771230054", "Dr. Gomes", 750.0,
                List.of(new TreatmentItem("Extraction", 2950.0)), "2025-12-18", "15:00", "Admin", "S001"));
        addIfMissing(new Appointment("A072", "Suranga Lakmal", "No. 10, Bellanthota Road, Angulana", "0771230055", "Dr. Gomes", 750.0,
                List.of(new TreatmentItem("Dental X-Ray", 2350.0)), "2026-02-18", "10:15", "User", "S002"));
        addIfMissing(new Appointment("A073", "Hansi Nirasha", "No. 47, Wewala Road, Piliyandala", "0771230056", "Dr. Gomes", 750.0,
                List.of(new TreatmentItem("Filling", 3150.0)), "2026-04-24", "12:45", "Admin", "S001"));
        addIfMissing(new Appointment("A074", "Kasun Maduranga", "No. 81, Weera Mawatha, Kolonnawa", "0771230057", "Dr. Gomes", 750.0,
                List.of(new TreatmentItem("Scaling", 3550.0)), "2026-06-26", "14:00", "User", "S002"));
        addIfMissing(new Appointment("A075", "Amaya Rajapaksha", "No. 3, Suwarapola Road, Attureliya", "0771230058", "Dr. Gomes", 750.0,
                List.of(new TreatmentItem("Whitening", 4600.0)), "2026-07-04", "15:45", "Admin", "S001"));
    }

    // only adds when the id is not in the database yet, so restarting never duplicates
    private void addIfMissing(Appointment appointment) {
        if (dao.findById(appointment.getId()) == null) {
            addAppointment(appointment);
        }
    }
}