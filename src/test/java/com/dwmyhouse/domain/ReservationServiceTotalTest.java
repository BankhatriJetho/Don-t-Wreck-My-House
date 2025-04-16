package com.dwmyhouse.domain;

import com.dwmyhouse.models.Host;
import com.dwmyhouse.models.Reservation;
import com.dwmyhouse.testutils.FakeReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReservationServiceTotalTest {

    ReservationService service;
    Host testHost;

    @BeforeEach
    void setup() {
        FakeReservationRepository fakeRepo = new FakeReservationRepository();
        service = new ReservationService(fakeRepo);

        testHost = new Host();
        testHost.setId("host-123");
        testHost.setStandardRate(new BigDecimal("100"));
        testHost.setWeekendsRate(new BigDecimal("150"));
    }

    private BigDecimal calculate(LocalDate start, LocalDate end) {
        Reservation reservation = new Reservation(0, start, end, "guest-1", null);
        reservation.setHostId(testHost.getId());
        service.makeReservation(reservation,testHost);
        return reservation.getTotal();
    }

    @Test
    void shouldCalculateAllWeekdays() {
        BigDecimal total = calculate(
                LocalDate.of(2025,4,27), //Sunday
                LocalDate.of(2025,5,2) //Friday
        );
        assertEquals(new BigDecimal("500"), total); //5 nights
    }

    @Test
    void shouldCalculateAllWeekends() {
        BigDecimal total = calculate(
                LocalDate.of(2025,4,25), //Friday
                LocalDate.of(2025,4,27) //Sunday
        );
        assertEquals(new BigDecimal("300"), total); // 2 weekends night
    }

    @Test
    void shouldCalculateMixedRate() {
        BigDecimal total = calculate(
                LocalDate.of(2025,4,24), //Thursday
                LocalDate.of(2025,4,28) //Monday
        );
        assertEquals(new BigDecimal("500"), total); //1 weekday night + 2 weekends nights + 1 weekday night
    }

    @Test
    void shouldCalculateOneNightStay() {
        BigDecimal total = calculate(
                LocalDate.of(2025,4,22), //Tuesday
                LocalDate.of(2025,4,23) //Wednesday
        );
        assertEquals(new BigDecimal("100"), total); //One weekday night
    }
}
