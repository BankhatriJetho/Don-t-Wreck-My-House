package com.dwmyhouse.ui;

import com.dwmyhouse.domain.GuestService;
import com.dwmyhouse.domain.HostService;
import com.dwmyhouse.domain.ReservationService;
import com.dwmyhouse.models.Guest;
import com.dwmyhouse.models.Host;
import com.dwmyhouse.models.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Handles main application flow and menu routing
 */

@Controller
public class MainController {

    private final GuestService guestService;
    private final HostService hostService;
    private final ReservationService reservationService;
    private final View view;
    private final Scanner console = new Scanner(System.in);

    @Autowired
    public MainController(GuestService guestService, HostService hostService,
                          ReservationService reservationService, View view) {
        this.guestService = guestService;
        this.hostService = hostService;
        this.reservationService = reservationService;
        this.view = view;
    }

    /**
     * Entry point of the application. Displays the main menu and handles user actions.
     */
    public void run() {
        view.displayMessage("Welcome to Don't Wreck My House");

        while(true) {
            view.displayMenu();
            String input = view.readMenuSelection("Select [0 - 4]: " );

            switch (input) {
                case "1":
                    viewReservations();
                    break;
                case "2":
                    makeReservation();
                    break;
                case "3":
                    editReservation();
                    break;
                case "4":
                    cancelReservation();
                    break;
                case "0":
                    view.displayMessage("Good Bye!");
                    return;
                default:
                    view.displayMessage("Invalid selection. Please try again.");
            }
        }
    }

    private void viewReservations() {
        String email = view.readRequiredString("Host Email: ");
        Host host = hostService.getHostByEmail(email);

        if(host == null) {
            view.displayMessage("Host not found.");
            return;
        }

        List<Reservation> reservations = reservationService.viewReservationsForHost(host.getId());

        if(reservations.isEmpty()) {
            view.displayMessage("No reservations found for this host.");
            return;
        }

        view.displayHeader(host.getLastName() + ": " + host.getCity() + ", " + host.getState());

        for(var r : reservations) {
            Guest guest = guestService.getGuestById(r.getGuestId());
            String guestName = (guest != null) ? guest.getLastName() + ", " + guest.getFirstName() : "Unknown Guest";

            String guestEmail = (guest != null) ? guest.getEmail() : "???";

            System.out.printf("ID: %d, %s - %s, Guest: %s, Email: %s%n",
                    r.getId(),
                    r.getStartDate(),
                    r.getEndDate(),
                    guestName,
                    guestEmail
            );
        }
    }

    private void makeReservation() {

        String guestEmail = view.readRequiredString("Guest Email: ");
        Guest guest = guestService.getGuestByEmail(guestEmail);
        if(guest == null) {
            view.displayMessage("Guess not found.");
            return;
        }

        String hostEmail = view.readRequiredString("Host Email: ");
        Host host = hostService.getHostByEmail(hostEmail);
        if(host == null) {
            System.out.println("Host not found.");
            return;
        }

        //checking what dates are already booked so admin doesn't cause overlaps
        List<Reservation> reservations = reservationService.viewReservationsForHost(host.getId());
        view.displayHeader(host.getLastName() + ": " + host.getCity() + ", " + host.getState());

        for (Reservation r : reservations) {
            if (r.getStartDate().isAfter(LocalDate.now())) {
                Guest resGuest = guestService.getGuestById(r.getGuestId());
                String guestName = (resGuest != null)
                        ? resGuest.getLastName() + ", " + resGuest.getFirstName()
                        : "Unknown Guest";
                String guestEmailDisplay = (resGuest != null) ? resGuest.getEmail() : "???";

                System.out.printf("ID: %d, %s - %s, Guest: %s, Email: %s%n",
                        r.getId(),
                        r.getStartDate(),
                        r.getEndDate(),
                        guestName,
                        guestEmailDisplay);
            }
        }

        try {
            LocalDate start = view.readDate("Start (yyyy-MM-dd): ");
            LocalDate end = view.readDate("End (yyyy-MM-dd): ");

            Reservation newRes = new Reservation(0, start, end, guest.getGuestId(),null);
            boolean success = reservationService.makeReservation(newRes, host);

            if(success) {
                view.displayReservationSummary(newRes);
                if(view.confirm("Is this okay? [y/n]: ")) {
                    view.displayMessage("Reservation created.");
                } else {
                    view.displayMessage("Reservation cancelled.");
                }
            } else {
                view.displayMessage("Invalid reservation. Possible overlap or bad date.");
            }
        } catch (Exception e) {
            System.out.println("Invalid date format.");
        }

    }

    private void editReservation(){
        String guestEmail = view.readRequiredString("Guest Email: ");
        Guest guest = guestService.getGuestByEmail(guestEmail);
        if(guest == null) {
            System.out.println("Guest not found.");
            return;
        }

        String hostEmail = view.readRequiredString("Host Email: ");
        Host host = hostService.getHostByEmail(hostEmail);
        if(host == null) {
            System.out.println("Host not found.");
            return;
        }

        List<Reservation> reservations = reservationService.viewReservationsForHost(host.getId());
        List<Reservation> guestReservations = reservations.stream()
                .filter(r -> r.getGuestId().equals(guest.getGuestId()))
                .toList();

        if(guestReservations.isEmpty()) {
            view.displayMessage("No reservations found for this guest at this host.");
            return;
        }

        view.displayReservations(guestReservations);

        try {
            int id = Integer.parseInt(view.readRequiredString("Reservation ID to edit: "));

            Reservation toEdit = guestReservations.stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElse(null);

            if(toEdit == null) {
                view.displayMessage("Reservation not found.");
                return;
            }

            LocalDate newStart = view.readDate("Start", toEdit.getStartDate());
            LocalDate newEnd = view.readDate("End", toEdit.getEndDate());

            toEdit.setStartDate(newStart);
            toEdit.setEndDate(newEnd);

            boolean success = reservationService.editReservation(toEdit, host);
            if(success) {
                view.displayReservationSummary(toEdit);
                view.displayMessage("Reservation updated successfully.");
            } else {
                view.displayMessage("Invalid reservation update. Check for overlap or bad dates.");
            }
        } catch (Exception e) {
            view.displayMessage("Invalid input. Reservation update cancelled.");
        }
    }

    private void cancelReservation() {
        String guestEmail = view.readRequiredString("Guest Email: ");
        Guest guest = guestService.getGuestByEmail(guestEmail);

        if(guest == null) {
           view.displayMessage("Guest not found.");
            return;
        }

        String hostEmail = view.readRequiredString("Host Email: ");
        Host host = hostService.getHostByEmail(hostEmail);

        if(host == null) {
            System.out.println("Host not found.");
            return;
        }

        List<Reservation> reservations = reservationService.viewReservationsForHost(host.getId());
        List<Reservation> futureGuestReservations = reservations.stream()
                .filter(r -> r.getGuestId().equals(guest.getGuestId()))
                .filter(r -> r.getStartDate().isAfter(LocalDate.now()))
                .toList();

        if(futureGuestReservations.isEmpty()) {
            view.displayMessage("No future reservations found for this guest at this host.");
            return;
        }

        view.displayReservations(futureGuestReservations);

        try {
            int id = Integer.parseInt(view.readRequiredString("Reservation ID to cancel: "));

            boolean success = reservationService.cancelReservation(id, host.getId());

            if(success) {
                view.displayMessage("Reservation cancelled successfully.");
            } else {
                view.displayMessage("Unable to cancel reservation. It may already be in the past.");
            }
        } catch (Exception e) {
            view.displayMessage("Invalid input. Cancellation failed.");
        }
    }
}
