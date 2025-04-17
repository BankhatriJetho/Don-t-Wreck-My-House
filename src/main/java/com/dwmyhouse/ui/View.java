package com.dwmyhouse.ui;

import com.dwmyhouse.models.Reservation;
import org.springframework.stereotype.Component;

import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Handles all console input/output (UI Layer)
 */
@Component
public class View {

    private static final DateTimeFormatter FLEXIBLE_DATE = DateTimeFormatter.ofPattern("yyyy-M-d");
    private final Scanner console;
    private final PrintStream out;

    public View() {
        this(new Scanner(System.in), System.out);
    }

    public View(Scanner console, PrintStream out) {
        this.console = console;
        this.out = out;
    }

    public void displayMenu() {
        System.out.println("\nMain Menu");
        System.out.println("============");
        System.out.println("0. Exit");
        System.out.println("1. View Reservations for Host");
        System.out.println("2. Make a Reservation");
        System.out.println("3. Edit a Reservation");
        System.out.println("4. Cancel a Reservation");
    }

    public void displayHeader(String header) {
        out.println("\n" + header);
        out.println("=".repeat(header.length()));
    }

    public String readMenuSelection(String prompt) {
        System.out.print(prompt);
        return console.nextLine().trim();
    }

    public String readRequiredString(String prompt) {
        String result;
        do {
            System.out.print(prompt);
            result = console.nextLine().trim();
        } while (result.isBlank());
        return result;
    }

    public String readString(String prompt) {
        System.out.print(prompt);
        return console.nextLine().trim();
    }

    public LocalDate readDate(String prompt, LocalDate defaultValue) {
        LocalDate result = null;
        while (result == null) {
            System.out.printf("%s (%s): ", prompt, defaultValue);
            String input = console.nextLine().trim();
            if (input.isEmpty()) {
                return defaultValue;
            }

            try {
                result = LocalDate.parse(input,FLEXIBLE_DATE);
            } catch (DateTimeParseException ex) {
                displayMessage("Invalid Date Format.");
            }
        }

        return result;
    }

    public LocalDate readDate(String prompt) {
        LocalDate result = null;
        while (result == null) {
            String input = readRequiredString(prompt + " (yy-MM-dd): ");
            try {
                result = LocalDate.parse(input, FLEXIBLE_DATE);
            } catch (DateTimeParseException ex) {
                displayMessage("Invalid date format.");
            }
        }
        return result;
    }

    public boolean confirm(String prompt) {
        System.out.print(prompt);
        String input = console.nextLine().trim();
        return input.equalsIgnoreCase("y");
    }

    public void displayReservations(List<Reservation> reservations) {
        if(reservations == null || reservations.isEmpty()) {
            out.println("No reservations found.");
            return;
        }
        for (Reservation r : reservations) {
            out.printf("ID: %s, %s to %s, Guest ID: %s, Total: $%s%n",
                    r.getId(),
                    r.getStartDate(),
                    r.getEndDate(),
                    r.getGuestId(),
                    r.getTotal());
        }
    }

    public void displayReservationSummary(Reservation r) {

            displayHeader("Summary");
            out.printf("Start: %s%n", r.getStartDate());
            out.printf("End: %s%n", r.getEndDate());
            if(!(r.getTotal() == null)) {
                out.printf("Total: $%s%n", r.getTotal());
            } else {
                out.printf("Total: $%s%n", 0.00);
            }
    }

    public void displayMessage(String message) {
        out.println(message);
    }

}
