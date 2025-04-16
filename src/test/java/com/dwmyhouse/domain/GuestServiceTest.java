package com.dwmyhouse.domain;

import com.dwmyhouse.data.GuestRepository;
import com.dwmyhouse.models.Guest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class GuestServiceTest {

    GuestService service;

    @BeforeEach
    void setup() {
        GuestRepository repo = new GuestRepository(Paths.get("data/guests.csv"));
        service = new GuestService(repo);
    }

    @Test
    void shouldFindGuestByEmail() {
        Guest guest = service.getGuestByEmail("tcarncross2@japanpost.jp");
        assertNotNull(guest);
        assertEquals("Carncross", guest.getLastName());
    }

    @Test
    void shouldReturnNullForUnknownEmail() {
        Guest guest = service.getGuestByEmail("nonexistent@email.com");
        assertNull(guest);
    }

    @Test
    void shouldReturnNullWhenEmailIsNull() {
        Guest guest = service.getGuestByEmail(null);
        assertNull(guest);
    }

    @Test
    void shouldReturnNullWhenEmailIsEmpty() {
        Guest guest = service.getGuestByEmail("");
        assertNull(guest);
    }
}
