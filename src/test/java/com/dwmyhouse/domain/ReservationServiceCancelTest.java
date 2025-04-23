package com.dwmyhouse.domain;

import com.dwmyhouse.models.Host;
import com.dwmyhouse.models.Reservation;
import com.dwmyhouse.testutils.FakeReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationServiceCancelTest {

    ReservationService service;
    FakeReservationRepository fakeRepo;
    Host testHost;

    @BeforeEach
    void setup() {
        fakeRepo = new FakeReservationRepository();
        service = new ReservationService(fakeRepo);

        testHost = new Host();
        testHost.setId("host-xyz");
        testHost.setStandardRate(new BigDecimal("100"));
        testHost.setWeekendsRate(new BigDecimal("150"));

        //Future reservation to cancel
        Reservation r1 = new Reservation(1,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(7),
                "guest-1",
                new BigDecimal("250"));
        r1.setHostId((testHost.getId()));
        fakeRepo.add(r1, testHost.getId());

        //Past reservation (should not cancel)
        Reservation r2 = new Reservation(2,
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(7),
                "guest-2",
                new BigDecimal("300"));
        r2.setHostId(testHost.getId());
        fakeRepo.add(r2, testHost.getId());
    }

    @Test
    void shouldCancelFutureReservation() {
        boolean result = service.cancelReservation(1, testHost.getId());
        assertTrue(result);

        List<Reservation> remaining = service.viewReservationsForHost(testHost.getId());
        assertEquals(1, remaining.size());
        assertEquals(2,remaining.get(0).getId()); //only past reservation should remain
    }

    @Test
    void shouldNotCancelPastReservation() {
        boolean result = service.cancelReservation(2, testHost.getId());
        assertFalse(result);

        List<Reservation> stillThere = service.viewReservationsForHost(testHost.getId());
        assertEquals(2, stillThere.size());
    }

    @Test
    void shouldNotCancelNonExistentReservation() {
        boolean result = service.cancelReservation(999, testHost.getId());
        assertFalse(result);
    }

    @Test
    void shouldReduceReservationCountAfterCancel() {
        int before = service.viewReservationsForHost(testHost.getId()).size();
        boolean result = service.cancelReservation(1, testHost.getId());
        int after = service.viewReservationsForHost(testHost.getId()).size();

        assertTrue(result);
        assertEquals(before - 1, after);
    }

    @Test
    void shouldNotCancelWhenHostIdIsNull() {
        boolean result = service.cancelReservation(1,null);
        assertFalse(result);
    }

    @Test
    void shouldOnlyCancelOnce() {
        //First cancel should work
        boolean first = service.cancelReservation(1, testHost.getId());

        //Second cancel should fail
        boolean second = service.cancelReservation(1, testHost.getId());

        assertTrue(first);
        assertFalse(second);
    }

    @Test
    void shouldNotCancelFromHostWithNoReservations() {
        Host emptyHost = new Host();
        emptyHost.setId("empty-host");

        boolean result = service.cancelReservation(1, emptyHost.getId());
        assertFalse(result);
    }
}
