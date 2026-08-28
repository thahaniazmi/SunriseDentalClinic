package com.dental.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.dental.database.DatabaseConnection;
import com.dental.model.Appointment;
import com.dental.model.TreatmentItem;

// uses a throwaway database file so the real data is never touched
public class AppointmentServiceImplTest {

    @BeforeAll
    public static void useTestDatabase() throws Exception {
        Path dir = Path.of("target", "testdb");
        Files.createDirectories(dir);
        Files.deleteIfExists(dir.resolve("test.db"));
        System.setProperty("dental.db", dir.resolve("test.db").toString());
        DatabaseConnection.resetForTests();
    }

    private Appointment validAppointment(String id, String name, String phone) {
        return new Appointment(id, name, "No. 12, Galle Road, Dehiwala", phone, "Dr. Anna", 500.0,
                List.of(new TreatmentItem("Cleaning", 1500.0)), "2026-08-20", "09:00", "Kasun Perera", "S003");
    }

    // ---------- validation: invalid data is rejected ----------

    @Test
    public void rejectsMissingPatientName() {
        AppointmentService service = new AppointmentServiceImpl();
        assertThrows(IllegalArgumentException.class,
                () -> service.addAppointment(validAppointment("T001", "", "0771234567")));
        assertThrows(IllegalArgumentException.class,
                () -> service.addAppointment(validAppointment("T002", "   ", "0771234567")));
    }

    @Test
    public void rejectsBadPhoneNumbers() {
        AppointmentService service = new AppointmentServiceImpl();
        assertThrows(IllegalArgumentException.class,
                () -> service.addAppointment(validAppointment("T003", "Nine Digits", "077123456")));
        assertThrows(IllegalArgumentException.class,
                () -> service.addAppointment(validAppointment("T004", "Eleven Digits", "07712345678")));
        assertThrows(IllegalArgumentException.class,
                () -> service.addAppointment(validAppointment("T005", "Has Letters", "077abc4567")));
    }

    // ---------- boundary: exactly 10 digits is the only phone that passes ----------

    @Test
    public void acceptsExactlyTenDigitPhone() {
        AppointmentService service = new AppointmentServiceImpl();
        Appointment saved = service.addAppointment(validAppointment("T010", "Boundary Patient", "0771234567"));
        assertNotNull(service.findById(saved.getId()));
    }

    // ---------- normal behaviour ----------

    @Test
    public void savesAndFindsAppointmentById() {
        AppointmentService service = new AppointmentServiceImpl();
        Appointment saved = service.addAppointment(validAppointment("T020", "Id Patient", "0777777777"));
        Appointment found = service.findById(saved.getId());
        assertNotNull(found);
        assertEquals("Id Patient", found.getPatientName());
        assertEquals(2000.0, found.getTotal());
    }

    @Test
    public void patientAddressIsSavedWithTheAppointment() {
        AppointmentService service = new AppointmentServiceImpl();
        service.addAppointment(validAppointment("T030", "Address Patient", "0765554443"));
        assertEquals("No. 12, Galle Road, Dehiwala", service.findById("T030").getPatientAddress());
    }

    // ---------- reports: the one reusable count function ----------

    @Test
    public void countForTodayMatchesSeededAppointments() {
        AppointmentService service = new AppointmentServiceImpl();
        String today = java.time.LocalDate.now().toString();
        assertEquals(5, service.countAppointments(today, today, null));
    }

    @Test
    public void countFiltersByDentistName() {
        AppointmentService service = new AppointmentServiceImpl();
        String today = java.time.LocalDate.now().toString();
        assertEquals(1, service.countAppointments(today, today, "Dr. Anna"));
        assertEquals(0, service.countAppointments(today, today, "Dr. Nobody"));
    }

    @Test
    public void searchFindsSeededAppointmentById() {
        AppointmentService service = new AppointmentServiceImpl();
        List<Appointment> results = service.search("A001");
        assertEquals(1, results.size());
        assertEquals("Kavindu Perera", results.get(0).getPatientName());
    }

    @Test
    public void searchFindsByPartOfPatientName() {
        AppointmentService service = new AppointmentServiceImpl();
        assertTrue(service.search("Kavindu").size() >= 1);
    }

    @Test
    public void emptySearchReturnsEverything() {
        AppointmentService service = new AppointmentServiceImpl();
        assertTrue(service.search("").size() >= 17);
    }

    @Test
    public void dateRangeSearchOnlyReturnsThatDay() {
        AppointmentService service = new AppointmentServiceImpl();
        List<Appointment> results = service.search("A002").isEmpty()
                ? List.of()
                : service.searchByDate(java.time.LocalDate.now().minusDays(7).toString(),
                        java.time.LocalDate.now().minusDays(7).toString());
        assertTrue(results.stream().allMatch(a -> a.getDate().equals(
                java.time.LocalDate.now().minusDays(7).toString())));
    }
}
