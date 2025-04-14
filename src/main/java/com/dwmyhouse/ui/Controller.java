package com.dwmyhouse.ui;

import com.dwmyhouse.domain.GuestService;
import com.dwmyhouse.domain.HostService;
import com.dwmyhouse.domain.ReservationService;
import com.dwmyhouse.models.Reservation;
import com.sun.security.jgss.GSSUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.PublicKey;
import java.util.Scanner;

/**
 * Handles main application flow and menu routing
 */

@Controller
public class Controller {

    private final GuestService guestService;
    private final HostService hostService;
    private final ReservationService reservationService;
    private final Scanner console = new Scanner(System.in);

    @Autowired
    public Controller(GuestService guestService, HostService hostService,
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
        //add more logic
    }

    private void makeReservation() {
        System.out.println("[Make Reservations]");
        //more logic here for making a reservation
    }

    private void editReservation(){
        System.out.println("[Edit Reservations]");
        //more logic here for edit
    }

    private void cancelReservation() {
        System.out.println("[Cancel Reservations]");
        //more logic here for cancellation
    }
}
