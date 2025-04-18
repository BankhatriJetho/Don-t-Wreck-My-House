package com.dwmyhouse.ui;

import com.dwmyhouse.domain.GuestService;
import com.dwmyhouse.domain.HostService;
import com.dwmyhouse.domain.ReservationService;
import com.dwmyhouse.models.Guest;
import com.dwmyhouse.models.Host;
import com.dwmyhouse.models.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
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

    @Autowired
    public MainController(GuestService guestService, HostService hostService,
                          ReservationService reservationService, View view) {
        this.guestService = guestService;
        this.hostService = hostService;
        this.reservationService = reservationService;
        this.view = view;
    }

    /**
     * Entry point of the application.
     * Displays the main menu and handles user actions.
     */
    public void run() {
        view.displayHeader("Welcome to Don't Wreck My House");
        System.out.println();
        view.displayMessage("Select from the Menu below to continue.");

        while(true) { //Displays menu and read user selection
            view.displayMenu();
            String input = view.readMenuSelection("Select [0 - 5]: " );

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
                case "5":
                    manageGuests();
                    break;
                case "6":
                    manageHosts();
                    break;
                case "0":
                    view.displayHeader("Exiting....");
                    view.displayMessage("Good Bye!");
                    return;
                default:
                    view.displayMessage("Invalid selection. Please try again.");
            }
        }
    }

    /**
     * Handles the flow to view reservations for a given host
     * Displays existing reservations and guest info if found
     */
    private void viewReservations() {
        view.displayHeader("View Reservations for Host");
        String email = view.readValidEmail("Host Email: ");
        Host host = hostService.getHostByEmail(email);

        if(host == null) {
            view.displayMessage("Host not found.");
            return;
        }

        // Retrieve and filter reservations for the host
        List<Reservation> reservations = reservationService.viewReservationsForHost(host.getId());
        reservations.sort(Comparator.comparing(Reservation::getStartDate));

        if(reservations.isEmpty()) {
            view.displayMessage("No reservations found for this host.");
            return;
        }

        view.displayHeader(host.getLastName() + ": " + host.getCity() + ", " + host.getState());

        for(var r : reservations) { // Loop through reservations and print guest details
            Guest guest = guestService.getGuestById(r.getGuestId());
            String guestName = (guest != null) ? guest.getLastName() + ", " + guest.getFirstName() : "Unknown Guest";

            String guestEmail = (guest != null) ? guest.getEmail() : "???";

            System.out.printf("ID: %-3d | Date: %-10s to %-10s | Guest: %-25s | Email: %-30s%n",
                    r.getId(),
                    r.getStartDate(),
                    r.getEndDate(),
                    guestName,
                    guestEmail
            );
        }
    }

    /**
     * Guides user through creating a new reservation
     * Validates date format and business rules before saving
     */
    private void makeReservation() {
        view.displayHeader("Add/Make a Reservation");
        String guestEmail = view.readValidEmail("Guest Email: ");
        Guest guest = guestService.getGuestByEmail(guestEmail);
        if(guest == null) {
            view.displayMessage("Guest not found.");
            return;
        }

        String hostEmail = view.readValidEmail("Host Email: ");
        Host host = hostService.getHostByEmail(hostEmail);
        if(host == null) {
            System.out.println("Host not found.");
            return;
        }

        List<Reservation> reservations = reservationService.viewReservationsForHost(host.getId());
        reservations.sort(Comparator.comparing(Reservation::getStartDate));
        view.displayReservations(reservations); //Show existing reservation for that host
        view.displayHeader(host.getLastName() + ": " + host.getCity() + ", " + host.getState());

        try {
            LocalDate start = view.readDate("Start (yyyy-MM-dd): ");
            LocalDate end = view.readDate("End (yyyy-MM-dd): ");

            Reservation newRes = new Reservation(0, start, end, guest.getGuestId(),null);

            if(!start.isAfter(LocalDate.now()) || !end.isAfter(start)) {
                view.displayReservationSummary(newRes);
                view.displayMessage("Reservation must be in the future.");
                return;
            }

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

    /**
     * Allows user to modify an existing reservation.
     * Requires guest and host match, and only allows valid changes.
     */
    private void editReservation(){
        view.displayHeader("Edit a Reservation");
        String guestEmail = view.readValidEmail("Guest Email: ");
        Guest guest = guestService.getGuestByEmail(guestEmail);
        if(guest == null) {
            System.out.println("Guest not found.");
            return;
        }

        String hostEmail = view.readValidEmail("Host Email: ");
        Host host = hostService.getHostByEmail(hostEmail);
        if(host == null) {
            System.out.println("Host not found.");
            return;
        }

        //Filter reservations for this guest-host combination
        List<Reservation> reservations = reservationService.viewReservationsForHost(host.getId());
        reservations.sort(Comparator.comparing(Reservation::getStartDate));
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
                view.displayMessage("Reservation " + toEdit.getId() + " updated successfully.");
            } else {
                view.displayMessage("Reservation update failed. Check for bad dates.");
            }
        } catch (Exception e) {
            view.displayMessage("Invalid input. Reservation update cancelled.");
        }
    }

    /**
     * Handles reservation cancellation logic.
     * Only allows cancelling future reservations for matched guest and host.
     */
    private void cancelReservation() {
        view.displayMessage("Cancel a Reservation");
        String guestEmail = view.readValidEmail("Guest Email: ");
        Guest guest = guestService.getGuestByEmail(guestEmail);

        if(guest == null) {
           view.displayMessage("Guest not found.");
            return;
        }

        String hostEmail = view.readValidEmail("Host Email: ");
        Host host = hostService.getHostByEmail(hostEmail);

        if(host == null) {
            System.out.println("Host not found.");
            return;
        }

        //Filter and retrieve guest's future reservations with that host
        List<Reservation> reservations = reservationService.viewReservationsForHost(host.getId());
        reservations.sort(Comparator.comparing(Reservation::getStartDate));
        List<Reservation> futureGuestReservations = reservations.stream()
                .filter(r -> r.getGuestId().equals(guest.getGuestId()))
                .filter(r -> r.getStartDate().isAfter(LocalDate.now()))
                .toList();

        if(futureGuestReservations.isEmpty()) {
            view.displayMessage("No future reservations found for this guest at this host.");
            return;
        }

        //Display reservations
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

    /**
     * Handles logic to manage guests operation such as create, edit, delete
     */
    private void manageGuests() {
        while (true) {
            view.displayGuestMenu();
            String choice = view.readMenuSelection("Select [0 -4]: ");

            switch (choice) {
                case "1":
                    viewAllGuests();
                    break;
                case "2":
                    addGuest();
                    break;
                case"3":
                    updateGuest();
                    break;
                case "4":
                    deleteGuest();
                    break;
                case "0":
                    return;
                default:
                    view.displayMessage("Invalid selection. Try again.");
            }
        }
    }

    //helper methods for guest operations
    private void viewAllGuests() {
        List<Guest> guests = guestService.findAll();
        view.displayGuestTable(guests);
    }

    private void addGuest() {
        view.displayHeader("Add Guest");
        Guest guest = view.readGuestInfo(null);
        if (guestService.addGuest(guest)) {
            view.displayMessage("Guest added successfully.");
        } else {
            view.displayMessage("Failed to add guest.");
        }
    }

    private void updateGuest() {
        view.displayHeader("Update Guest");
        String id = view.readRequiredString("Guest ID to update: ");
        Guest existing = guestService.getGuestById(id);
        if (existing == null) {
            view.displayMessage("Guest not found.");
            return;
        }

        Guest updated = view.readGuestInfo(existing);
        if (guestService.updateGuest(updated)) {
            view.displayMessage("Guest updated successfully.");
        } else {
            view.displayMessage("Update failed.");
        }
    }

    private void deleteGuest() {
        view.displayHeader("Delete Guest");
        String id = view.readRequiredString("Guest ID to delete: ");
        if (guestService.deleteGuest(id)) {
            view.displayMessage("Guest deleted successfully.");
        } else {
            view.displayMessage("Delete failed.");
        }
    }

    private void manageHosts() {
        while (true) {
            view.displayHostMenu();
            String choice = view.readMenuSelection("Select [0 - 4]: ");

            switch (choice) {
                case "1":
                    viewAllHosts();
                    break;
                case "2":
                    addHost();
                    break;
                case "3":
                    updateHost();
                    break;
                case "4":
                    deleteHost();
                    break;
                case "0":
                    return;
                default:
                    view.displayMessage("Invalid selection. Try again.");
            }
        }
    }

    private void viewAllHosts() {
        List<Host> hosts = hostService.findAll();
        view.displayHostTable(hosts);
    }

    private void addHost() {
        System.out.println("Inside addHost()"); //debug
        view.displayHeader("Add Host");
        Host newHost = view.readHostInfo(null);

        System.out.println("Validating host: " + newHost.getEmail()); //debug

        boolean duplicate = hostService.findAll().stream()
                .anyMatch(h -> h.getEmail().equalsIgnoreCase(newHost.getEmail().trim()));

        List<String> errors = validateHost(newHost);

        if (duplicate) {
            errors.add("A host with this email already exists. Use different email.");
        }

        System.out.println("Errors found: " + errors.size()); // debug

        if (!errors.isEmpty()) {
            view.displayHeader("Validation Failed");
            errors.forEach(view::displayMessage);
            return;
        }

        boolean added = hostService.addHost(newHost);
        System.out.println("addHost() returned: " + added); //debug line

        if (added) {
            view.displayMessage("Host added successfully.");
        } else {
            view.displayMessage("Failed to add host.");
        }
    }

    private void updateHost() {
        view.displayHeader("Update Host");
        String id = view.readRequiredString("Host ID to update: ");
        Host existing = hostService.findAll().stream()
                .filter(h -> h.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (existing == null) {
            view.displayMessage("Host not found.");
            return;
        }

        Host updated = view.readHostInfo(existing);
        if (hostService.updateHost(updated)) {
            view.displayMessage("Host updated successfully.");
        } else {
            view.displayMessage("Update failed.");
        }
    }

    private void deleteHost() {
        view.displayHeader("Delete Host");
        String id = view.readRequiredString("Host ID to delete: ");
        if (hostService.deleteHost(id)) {
            view.displayMessage("Host deleted successfully.");
        } else {
            view.displayMessage("Delete failed.");
        }
    }

    private List<String> validateHost(Host host) {
        List<String> errors = new ArrayList<>();

        if (host.getEmail() == null || !host.getEmail().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            errors.add("Invalid email format.");
        }

        if (host.getCity() == null || host.getCity().isBlank()) {
            errors.add("City is required.");
        }

        if (host.getState() == null || host.getState().isBlank()) {
            errors.add("State is required.");
        }

        if (host.getStandardRate() == null || host.getStandardRate().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Standard rate must be a positive number.");
        }

        if (host.getWeekendsRate() == null || host.getWeekendsRate().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Weekend rate must be a positive number.");
        }

        return errors;
    }


}
