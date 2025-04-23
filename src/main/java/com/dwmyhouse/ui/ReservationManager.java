package com.dwmyhouse.ui;

import com.dwmyhouse.domain.GuestService;
import com.dwmyhouse.domain.HostService;
import com.dwmyhouse.domain.ReservationService;
import com.dwmyhouse.models.Guest;
import com.dwmyhouse.models.Host;
import com.dwmyhouse.models.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReservationManager {

    private final ReservationService reservationService;
    private final HostService hostService;
    private final GuestService guestService;
    private final View view;

    @Autowired
    public ReservationManager(
            ReservationService reservationService,
            HostService hostService,
            GuestService guestService,
            View view) {
        this.reservationService = reservationService;
        this.hostService = hostService;
        this.guestService = guestService;
        this.view = view;
    }

    public void manageReservationFilters() {
        while (true) {
            view.displayReservationFilterMenu();
            String choice = view.readMenuSelection("Select [0 - 4]: ");

            switch (choice) {
                case "1":
                    viewByGuestEmail();
                    break;
                case "2":
                    viewByState();
                    break;
                case "3":
                    viewByCity();
                    break;
                case "4":
                    viewByZipCode();
                case "0":
                    return;
                default:
                    view.displayMessage("Invalid Selection.");
            }
        }
    }

    private void viewByGuestEmail() {
        view.displayHeader("View Reservations by Guest Email");
        String email = view.readValidEmail("Guest Email: ");
        Guest guest = guestService.getGuestByEmail(email);

        if(guest == null) {
            view.displayMessage("Guest not found.");
            return;
        }

        List<Host> hosts = hostService.findAll();

        List<Reservation> matchingReservations = hosts.stream()
                .flatMap(h -> reservationService.viewReservationsForHost(h.getId()).stream())
                .filter(r -> r.getGuestId().equals(guest.getGuestId()))
                .collect(Collectors.toList());

        if(matchingReservations.isEmpty()) {
            view.displayMessage("No reservations found for this guest.");
        } else {
            view.displayReservations(matchingReservations);
        }
    }

    private void viewByState() {
        view.displayHeader("view Reservations by Host State");
        String state = view.readRequiredString("Enter state (e.g., NY): ");

        List<Host> filteredHosts = hostService.findAll().stream()
                .filter(h -> h.getState().equalsIgnoreCase(state))
                .collect(Collectors.toList());

        displayReservationsForHosts(filteredHosts);
    }

    private void viewByCity() {
        view.displayHeader("View Reservations by Host City");
        String city = view.readRequiredString("Enter city: ");

        List<Host> filteredHosts = hostService.findAll().stream()
                .filter(h -> h.getCity().equalsIgnoreCase(city))
                .collect(Collectors.toList());

        displayReservationsForHosts(filteredHosts);
    }

    private void viewByZipCode() {
        view.displayHeader("View Reservations by Host Zip Code");
        String zip = view.readRequiredString("Enter zip code: ");

        List<Host> filteredHosts = hostService.findAll().stream()
                .filter(h -> h.getPostalCode().equals(zip))
                .collect(Collectors.toList());

        displayReservationsForHosts(filteredHosts);
    }

    private void displayReservationsForHosts(List<Host> hosts) {
        List<Reservation> reservations = hosts.stream()
                .flatMap(h -> reservationService.viewReservationsForHost(h.getId()).stream())
                .collect(Collectors.toList());

        if (reservations.isEmpty()) {
            view.displayMessage("No reservations found for selected filter.");
        } else {
            view.displayReservations(reservations);
        }
    }
}
