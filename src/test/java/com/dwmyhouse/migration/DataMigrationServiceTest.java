package com.dwmyhouse.migration;

import com.dwmyhouse.data.*;
import com.dwmyhouse.models.Guest;
import com.dwmyhouse.models.Host;
import com.dwmyhouse.models.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataMigrationServiceTest {

    GuestRepository mockGuestRepo;
    HostRepository mockHostRepo;
    ReservationRepository mockResRepo;

    GuestJsonRepository guestJsonRepo;
    HostJsonRepository hostJsonRepo;
    ReservationJsonRepository resJsonRepo;

    DataMigrationService service;

    @BeforeEach
    void setup(@TempDir Path tempDir) {
        // 1. Mock CSV-based repos
        mockGuestRepo = Mockito.mock(GuestRepository.class);
        mockHostRepo = Mockito.mock(HostRepository.class);
        mockResRepo = Mockito.mock(ReservationRepository.class);

        // 2. Real JSON-based repos using temp files
        guestJsonRepo = new GuestJsonRepository(tempDir.resolve("guests.json").toString());
        hostJsonRepo = new HostJsonRepository(tempDir.resolve("hosts.json").toString());
        resJsonRepo = new ReservationJsonRepository(tempDir.resolve("reservations.json").toString());

        // 3. The migration service
        service = new DataMigrationService(
                mockGuestRepo, guestJsonRepo,
                mockHostRepo, hostJsonRepo,
                mockResRepo, resJsonRepo
        );
    }

    @Test
    void shouldMigrateGuestsCorrectly() {
        Guest guest = new Guest("G001", "John", "Doe", "john@example.com", "111-111-1111", "NY");
        Mockito.when(mockGuestRepo.findAll()).thenReturn(List.of(guest));

        service.migrateGuests();

        List<Guest> migrated = guestJsonRepo.findAll();
        assertEquals(1, migrated.size());
        assertEquals("G001", migrated.get(0).getGuestId());

    }

    @Test
    void shouldMigrateReservationsCorrectly() {
        Host host = new Host("H001", "Smith", "smith@example.com", "123-456-7890",
                "123 Main", "Albany", "NY", "12207",
                new BigDecimal("100"), new BigDecimal("150"));
        Reservation res = new Reservation(0, LocalDate.now(), LocalDate.now().plusDays(2), "G001", new BigDecimal("300"));
        res.setHostId("H001");

        Mockito.when(mockHostRepo.findAll()).thenReturn(List.of(host));
        Mockito.when(mockResRepo.findByHost("H001")).thenReturn(List.of(res));

        service.migrateReservations();

        List<Reservation> migrated = resJsonRepo.findAll();
        assertEquals(1, migrated.size());
        assertEquals("H001", migrated.get(0).getHostId());
        assertEquals("G001", migrated.get(0).getGuestId());
    }

    @Test
    void shouldMigrateHostsCorrectly() {
        Host host = new Host("H001", "Smith", "smith@example.com", "123-456-7890",
                "123 Main", "Albany", "NY", "12207",
                new BigDecimal("100"), new BigDecimal("150"));

        Mockito.when(mockHostRepo.findAll()).thenReturn(List.of(host));

        service.migrateHosts();

        List<Host> migrated = hostJsonRepo.findAll();
        assertEquals(1, migrated.size());
        assertEquals("H001", migrated.get(0).getId());
        assertEquals("Smith", migrated.get(0).getLastName());
    }

}
