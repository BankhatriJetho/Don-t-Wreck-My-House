package com.dwmyhouse.ui;

import com.dwmyhouse.domain.GuestService;
import com.dwmyhouse.domain.HostService;
import com.dwmyhouse.domain.ReservationService;
import com.dwmyhouse.models.Guest;
import com.dwmyhouse.models.Host;
import com.dwmyhouse.models.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReservationManagerTest {

    @Mock
    ReservationService reservationService;

    @Mock
    GuestService guestService;

    @Mock
    HostService hostService;

    @Mock
    View view;

    @InjectMocks
    ReservationManager manager;

    Guest guest;
    Host host;
    Reservation res;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        guest = new Guest("G001", "John", "Doe",
                "john@example.com", "111-111-1111", "NY");

        host = new Host("H001","Smith","smith@example.com",
                "123-456-7890", "123 Main", "Albany","NY",
                "12207", new BigDecimal("100"), new BigDecimal("150"));

        res = new Reservation(1, LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3), "G001", new BigDecimal("200"));
        res.setHostId("H001");
    }


    @Test
    void shouldFilterByState() {
        when(view.readMenuSelection(any())).thenReturn("2", "0"); // simulate state filter then exit
        when(view.readRequiredString(any())).thenReturn("NY");
        when(hostService.findAll()).thenReturn(List.of(host));
        when(reservationService.viewReservationsForHost("H001")).thenReturn(List.of(res));

        manager.manageReservationFilters();

        verify(view).displayReservations(List.of(res));
    }

    @Test
    void shouldFilterByCity() {
        when(view.readMenuSelection(any())).thenReturn("3", "0");
        when(view.readRequiredString(any())).thenReturn("Albany");
        when(hostService.findAll()).thenReturn(List.of(host));
        when(reservationService.viewReservationsForHost("H001")).thenReturn(List.of(res));

        manager.manageReservationFilters();

        verify(view).displayReservations(List.of(res));
    }

    @Test
    void shouldFilterByZip() {
        when(view.readMenuSelection(any())).thenReturn("4", "0");
        when(view.readRequiredString(any())).thenReturn("12207");
        when(hostService.findAll()).thenReturn(List.of(host));
        when(reservationService.viewReservationsForHost("H001")).thenReturn(List.of(res));

        manager.manageReservationFilters();

        verify(view).displayReservations(List.of(res));
    }
}
