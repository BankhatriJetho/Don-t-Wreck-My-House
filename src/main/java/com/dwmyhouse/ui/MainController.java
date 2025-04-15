package com.dwmyhouse.ui;

import com.dwmyhouse.domain.GuestService;
import com.dwmyhouse.domain.HostService;
import com.dwmyhouse.domain.ReservationService;
import com.dwmyhouse.models.Guest;
import com.dwmyhouse.models.Host;
import com.dwmyhouse.models.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.security.spec.RSAOtherPrimeInfo;
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
    private final Scanner console = new Scanner(System.in);

    @Autowired
    public MainController(GuestService guestService, HostService hostService,
                          ReservationService reservationService) {
        this.guestService = guestService;
        this.hostService = hostService;
        this.reservationService = reservationService;
    }

    /**
     * Entry point of the application. Displays the main menu and handles user actions.
     */
    public void run() {
        System.out.println("Welcome to Don't Wreck My House");

        while(true) {
            displayMenu();
            String input = console.nextLine().trim();

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
                    System.out.println("Good Bye!");
                    return;
                default:
                    System.out.println("Invalid selection. Please try again.");
            }
        }
    }

    private void displayMenu() {
        System.out.println("\nMain Menu");
        System.out.println("============");
        System.out.println("0. Exit");
        System.out.println("1. View Reservations for Host");
        System.out.println("2. Make a Reservation");
        System.out.println("3. Edit a Reservation");
        System.out.println("4. Cancel a Reservation");
        System.out.println("Select [0 - 4]");
    }

    private void viewReservations() {
        System.out.println("[View Reservations]");
        System.out.println("Host Email: ");
        String email = console.nextLine().trim();

        var host = hostService.getHostByEmail(email);
        if(host == null) {
            System.out.println("Host not found.");
            return;
        }

        var reservations = reservationService.viewReservationsForHost(host.getId());
        if(reservations.isEmpty()) {
            System.out.println("No reservations found for this host.");
            return;
        }

        System.out.println("\n" + host.getLastName() + ": " + host.getCity() + ", " + host.getState());
        System.out.println("=".repeat(40));

        for(var r : reservations) {
            var guest = guestService.getGuestById(r.getGuestId());
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
        System.out.println("[Make Reservations]");
        System.out.println("Guest Email: ");
        String guestEmail = console.nextLine().trim();

        Guest guest = guestService.getGuestByEmail(guestEmail);

        if(guest == null) {
            System.out.println("Guest not found.");
            return;
        }
        System.out.println("Host Email: ");
        String hostEmail = console.nextLine().trim();

        Host host = hostService.getHostByEmail(hostEmail);

        if(host == null) {
            System.out.println("Host not found.");
            return;
        }

        //checking what dates are already booked so admin doesn't cause overlaps
        List<Reservation> reservations = reservationService.viewReservationsForHost(host.getId());

        System.out.println("\n" + host.getLastName() + ": " + host.getCity() + ", " + host.getState());
        System.out.println("=".repeat(40));

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
                        guestEmail);
            }
        }

        try {
            System.out.println("Start (yyyy-MM-dd): ");
            LocalDate start = LocalDate.parse(console.nextLine().trim());

            System.out.println("End (yyyy-MM-dd): ");
            LocalDate end = LocalDate.parse(console.nextLine().trim());

            Reservation newRes = new Reservation(0, start, end, guest.getGuestId(),null);
            boolean success = reservationService.makeReservation(newRes, host);

            if(success) {
                System.out.println("\nSummary");
                System.out.println("==========");
                System.out.printf("Start: %s%n", newRes.getStartDate());
                System.out.printf("End: %s%n", newRes.getEndDate());
                System.out.printf("Total: $%s%n", newRes.getTotal());

                System.out.println("Is this okay? [y/n]: ");
                String confirm = console.nextLine().trim();
                if(confirm.equalsIgnoreCase("y")) {
                    System.out.println("\nSuccess");
                    System.out.println("==========");
                    System.out.println("Reservation created.");
                } else {
                    System.out.println("Reservation cancelled.");
                }
            } else {
                System.out.println("Invalid reservation. Possible overlap or bad dates.");
            }
        } catch (Exception e) {
            System.out.println("Invalid date format.");
        }

    }

    private void editReservation(){
        System.out.println("[Edit Reservations]");
        System.out.println("Guest Email: ");
        String guestEmail = console.nextLine().trim();
        Guest guest = guestService.getGuestByEmail(guestEmail);

        if(guest == null) {
            System.out.println("Guest not found.");
            return;
        }

        System.out.println("Host Email: ");
        String hostEmail = console.nextLine().trim();
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
            System.out.println("No reservations found for this guest at this host.");
            return;
        }

        for(Reservation r: guestReservations) {
            System.out.printf("ID: %d, %s - %s%n", r.getId(), r.getStartDate(), r.getEndDate());
        }

        System.out.println("Reservation ID to edit: ");
        int id = Integer.parseInt(console.nextLine().trim());

        Reservation toEdit = guestReservations.stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElse(null);

        if(toEdit == null) {
            System.out.println("Reservation not found.");
            return;
        }

        System.out.printf("Start (%s): ", toEdit.getStartDate());
        String startInput = console.nextLine().trim();

        System.out.printf("End (%s): ", toEdit.getEndDate());
        String endInput = console.nextLine().trim();

        try {
            if(!startInput.isEmpty()) {
                toEdit.setStartDate(LocalDate.parse(startInput));
            }
            if(!endInput.isEmpty()) {
                toEdit.setEndDate(LocalDate.parse(endInput));
            }

            boolean success = reservationService.editReservation(toEdit, host);
            if(success) {
                System.out.println("\nSummary");
                System.out.println("===========");
                System.out.printf("Start: %s%n", toEdit.getStartDate());
                System.out.printf("End: %s%n", toEdit.getEndDate());
                System.out.printf("Total: $%s%n", toEdit.getTotal());
                System.out.println("Reservation updated successfully.");
            } else {
                System.out.println("Invalid reservation update. Check for overlap or bad dates.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Reservation update cancelled.");
        }
    }

    private void cancelReservation() {
        System.out.println("[Cancel Reservations]");
        System.out.println("Guest Email: ");
        String guestEmail = console.nextLine().trim();
        Guest guest = guestService.getGuestByEmail(guestEmail);

        if(guest == null) {
            System.out.println("Guest not found.");
            return;
        }

        System.out.println("Host Email: ");
        String hostEmail = console.nextLine().trim();
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
            System.out.println("No future reservations found for this guest at this host.");
            return;
        }

        for(Reservation r : futureGuestReservations) {
            System.out.printf("ID: %d, %s - %s%n", r.getId(), r.getStartDate(), r.getEndDate());
        }

        System.out.println("Reservation ID to cancel: ");
        int id = Integer.parseInt(console.nextLine().trim());

        boolean success = reservationService.cancelReservation(id, host.getId());

        if(success) {
            System.out.println("Reservation cancelled successfully.");
        } else {
            System.out.println("Unable to cancel reservation. It may already be in the past.");
        }
    }
}
