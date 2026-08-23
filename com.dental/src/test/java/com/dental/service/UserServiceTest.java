package com.dental.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.dental.model.User;

// uses a throwaway database file so the real data is never touched
public class UserServiceTest {

    @BeforeAll
    public static void useTestDatabase() throws Exception {
        Path dir = Path.of("target", "testdb");
        Files.createDirectories(dir);
        Files.deleteIfExists(dir.resolve("test.db"));
        System.setProperty("dental.db", dir.resolve("test.db").toString());
        com.dental.database.DatabaseConnection.resetForTests();
    }

    private final UserService service = new UserService();

    @Test
    public void findsSeededStaffByUsername() {
        User admin = service.findByUsername("admin@sunshine.lk");
        assertNotNull(admin);
        assertEquals("S001", admin.getStaffId());
        assertEquals("Admin", admin.getRole());
    }

    @Test
    public void unknownUsernameReturnsNull() {
        assertNull(service.findByUsername("nobody@sunshine.lk"));
    }

    @Test
    public void addedUserCanBeFoundThenRemoved() {
        service.addUser(new User("T099", "Temp Staff", "temp999@sunshine.lk", "1234", "Staff"));
        assertNotNull(service.findByUsername("temp999@sunshine.lk"));
        service.removeUser("T099");
        assertNull(service.findByUsername("temp999@sunshine.lk"));
    }
}
