package com.dwmyhouse.domain;

import com.dwmyhouse.models.Host;
import com.dwmyhouse.models.Reservation;
import com.dwmyhouse.testutils.FakeReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationServiceMakeTest {

    ReservationService service;
    FakeReservationRepository fakeRepo;
    Host testHost;

    @BeforeEach
    void setup() {

        fakeRepo = new FakeReservationRepository();
        fakeRepo.clearAll();
        service = new ReservationService(fakeRepo);

        testHost = new Host();
        testHost.setId("host-123");
        testHost.setStandardRate(new BigDecimal("100"));
        testHost.setWeekendsRate(new BigDecimal("150"));
    }

    @Test
    void shouldMakeValidReservation() {
        Reservation reservation = new Reservation(0,
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(5),
                "guest-1",
                null);

        boolean result = service.makeReservation(reservation, testHost);
        assertTrue(result);
        assertEquals(new BigDecimal("250"), reservation.getTotal());
    }

    @Test
    void shouldNotMakeOverlappingReservation() {
        // Add existing one to fake repo
        Reservation existing = new Reservation(1,
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(6),
                "guest-1",
                new BigDecimal("300"));

        fakeRepo.add(existing, testHost.getId());

        Reservation overlap = new Reservation(0,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(7),
                "guest-2",
                null);

        boolean result = service.makeReservation(overlap, testHost);
        assertFalse(result);
    }

    @Test
    void shouldNotMakeReservationWithPastStartDate() {
        Reservation reservation = new Reservation(0,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(2),
                "guest-1",
                null);

        assertFalse(service.makeReservation(reservation, testHost));
    }

    @Test
    void shouldFailWhenGuestIdIsNull() {
        Reservation reservation = new Reservation(0,
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(5),
                null, null);
        reservation.setHostId(testHost.getId());

        boolean result = service.makeReservation(reservation, testHost);
        assertFalse(result);
    }

    @Test
    void shouldFailWhenHostIdIsNull() {
        Reservation reservation = new Reservation(0,
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(5),
                "guest-1", null);

        boolean result = service.makeReservation(reservation, null);
        assertFalse(result);
    }

    @Test
    void shouldFailWhenStartDateAndEndDateAreSame() {
        LocalDate day = LocalDate.now().plusDays(3);
        Reservation reservation = new Reservation(0, day, day,"guest-1", null);
        reservation.setHostId(testHost.getId());

        boolean result = service.makeReservation(reservation, testHost);
        assertFalse(result);
    }

    @Test
    void shouldCalculateTotalForOneNightWeekday() {
        LocalDate start = LocalDate.of(2025,4,16); //weekday
        Reservation reservation = new Reservation(0, start, start.plusDays(1), "guest-1", null);
        reservation.setHostId(testHost.getId());

        boolean result = service.makeReservation(reservation, testHost);

        assertTrue(result);
        assertEquals(new BigDecimal("100"), reservation.getTotal());
    }

    @Test
    void shouldCalculateTotalForWeekendStay() {
        Reservation reservation = new Reservation(0,
                LocalDate.of(2025,4,18), //Friday
                LocalDate.of(2025,4,20), //Saturday
                "guest-1",
                null);
        reservation.setHostId(testHost.getId());

        boolean result = service.makeReservation(reservation, testHost);

        assertTrue(result);
        assertEquals(new BigDecimal("300"), reservation.getTotal());
    }
}
