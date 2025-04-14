package com.dwmyhouse.domain;

import com.dwmyhouse.models.Reservation;
import com.dwmyhouse.testutils.FakeReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReservationServiceViewTest {

    ReservationService service;
    FakeReservationRepository fakeRepo;

    @BeforeEach
    void setup() {

        fakeRepo = new FakeReservationRepository();
        fakeRepo.clearAll();
        service = new ReservationService(fakeRepo);

        fakeRepo.add(new Reservation(1,
                LocalDate.of(2025,6,1),
                LocalDate.of(2025,6,5),
                "101",
                null),"host-abc");

        fakeRepo.add(new Reservation(2,
                LocalDate.of(2025,7,1),
                LocalDate.of(2025,7,3),
                "102",
                null), "host-abc");

        fakeRepo.add(new Reservation(3,
                LocalDate.of(2025,8,1),
                LocalDate.of(2025,8,4),
                "103",
                null),"host-other");
    }

    @Test
    void shouldReturnReservationsForMatchingHost() {
        List<Reservation> result = service.viewReservationsForHost("host-abc");
        assertEquals(2, result.size());
        assertEquals(1,result.get(0).getId());
        assertEquals(2,result.get(1).getId());
    }

    @Test
    void shouldReturnEmptyListForHostWithNoReservations() {
        List<Reservation> result = service.viewReservationsForHost("host-none");
        assertTrue(result.isEmpty());
    }
}
