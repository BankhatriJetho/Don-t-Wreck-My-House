package com.dwmyhouse.domain;

import com.dwmyhouse.models.Host;
import com.dwmyhouse.models.Reservation;
import com.dwmyhouse.testutils.FakeReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationServiceEditTest {

    ReservationService service;
    FakeReservationRepository fakeRepo;
    Host testHost;

    @BeforeEach
    void setup() {
        fakeRepo = new FakeReservationRepository();
        service = new ReservationService(fakeRepo);

        testHost = new Host();
        testHost.setId("host-123");
        testHost.setStandardRate(new BigDecimal("100"));
        testHost.setWeekendsRate(new BigDecimal("150"));

        Reservation existing = new Reservation(1,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(8),
                "guest-1",
                new BigDecimal("450"));
        existing.setHostId(testHost.getId());
        fakeRepo.add(existing, testHost.getId());
    }

    @Test
    void shouldEditReservationWithValidDates() {
        Reservation toEdit = new Reservation(1,
                LocalDate.of(2025,4,24),
                LocalDate.of(2025,4,26),
                "guest-1",
                null);
        toEdit.setHostId(testHost.getId());

        boolean result = service.editReservation(toEdit, testHost);
        assertTrue(result);
        assertNotNull(toEdit.getTotal());
        assertEquals(new BigDecimal("250"), toEdit.getTotal()); // 2 nights (1 weekday, 1 weekend)
    }

    @Test
    void shouldFailEditIfOverlapping() {
        //Overlapping reservation
        Reservation existing = new Reservation(2,
                LocalDate.now().plusDays(6),
                LocalDate.now().plusDays(9),
                "guest-2",
                new BigDecimal("300"));
        existing.setHostId(testHost.getId());
        fakeRepo.add(existing, testHost.getId());

        //try edit reservation 1 to overlap with reservation 2
        Reservation toEdit = new Reservation(1,
                LocalDate.now().plusDays(6),
                LocalDate.now().plusDays(9),
                "guest-1",
                null);
        toEdit.setHostId(testHost.getId());

        boolean result = service.editReservation(toEdit, testHost);
        assertFalse(result); //Should fail due to overlap with reservation 2
    }

    @Test
    void shouldFailEditIfStartAfterEnd() {
        Reservation toEdit = new Reservation(1,
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(8),
                "guest-1",
                null);
        toEdit.setHostId(testHost.getId());

        boolean result = service.editReservation(toEdit, testHost);
        assertFalse(result);
    }

    @Test
    void shouldFailEditIfStartDateInPast() {
        Reservation toEdit = new Reservation(1,
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(1),
                "guest-1",
                null);
        toEdit.setHostId(testHost.getId());

        boolean result = service.editReservation(toEdit, testHost);
        assertFalse(result);
    }

    @Test
    void shouldEditWithSameDatesIfStillValid() {
        Reservation toEdit = new Reservation(1,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(8),
                "guest-1",
                null);
        toEdit.setHostId((testHost.getId()));

        boolean result = service.editReservation(toEdit, testHost);
        assertTrue(result);
        assertNotNull(toEdit.getTotal());
    }
}
