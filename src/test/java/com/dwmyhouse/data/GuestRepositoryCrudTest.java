package com.dwmyhouse.data;

import com.dwmyhouse.models.Guest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GuestRepositoryCrudTest {

    GuestRepository repository;
    Path testFile;

    @BeforeEach
    void setup(@TempDir Path tempDir) throws IOException {
        testFile = tempDir.resolve("guests.csv");

        try (PrintWriter writer = new PrintWriter(testFile.toFile())) {
            writer.println("guest_id,first_name,last_name,email,phone,state");
            writer.println("G001,John,Doe,john@example.com,111-111-1111,NY");
            writer.println("G002,Jane,Smith,jane@example.com,222-222-2222,CA");
        }

        repository = new GuestRepository(testFile.toString());
    }

    @Test
    void shouldReadAllGuests() {
        List<Guest> guests = repository.findAll();
        assertEquals(2, guests.size());
    }

    @Test
    void shouldFindGuestById() {
        Guest guest = repository.findById("G001");
        assertNotNull(guest);
        assertEquals("John", guest.getFirstName());
    }

    @Test
    void shouldFindGuestByEmail() {
        Guest guest = repository.findByEmail("jane@example.com");
        assertNotNull(guest);
        assertEquals("G002", guest.getGuestId());
    }

    @Test
    void shouldAddGuest() {
        Guest newGuest = new Guest();
        newGuest.setGuestId("G003");
        newGuest.setFirstName("Alice");
        newGuest.setLastName("Jones");
        newGuest.setEmail("alice@example.com");
        newGuest.setPhone("333-333-3333");
        newGuest.setState("TX");

        assertTrue(repository.add(newGuest));

        List<Guest> guests = repository.findAll();
        assertEquals(3, guests.size());
    }

    @Test
    void shouldUpdateGuest() {
        Guest existing = repository.findById("G001");
        existing.setPhone("999-999-9999");

        assertTrue(repository.update(existing));

        Guest updated = repository.findById("G001");
        assertEquals("999-999-9999", updated.getPhone());
    }

    @Test
    void shouldDeleteGuest() {
        assertTrue(repository.delete("G002"));
        assertNull(repository.findById("G002"));
    }

}
