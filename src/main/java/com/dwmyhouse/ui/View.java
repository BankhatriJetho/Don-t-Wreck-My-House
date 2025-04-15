package com.dwmyhouse.ui;

import com.dwmyhouse.models.Reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Handles all console input/output (UI Layer)
 */
public class View {

    private final Scanner console = new Scanner(System.in);

    public void displayMenu() {
        System.out.println("\nMain Menu");
        System.out.println("============");
        System.out.println("0. Exit");
        System.out.println("1. View Reservations for Host");
        System.out.println("2. Make a Reservation");
        System.out.println("3. Edit a Reservation");
        System.out.println("4. Cancel a Reservation");
        System.out.println("Select [0 - 4]");
    }

    public void displayHeader(String header) {
        System.out.println("\n" + header);
        System.out.println("=".repeat(header.length()));
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
        System.out.printf("%s (%s): ", prompt, defaultValue);
        String input = console.nextLine().trim();
        if (input.isEmpty()) {
            return defaultValue;
        }
        return LocalDate.parse(input);
    }

    public LocalDate readDate(String prompt) {
        System.out.print(prompt);
        return LocalDate.parse(console.nextLine().trim());
    }

    public boolean confirm(String prompt) {
        System.out.print(prompt);
        String input = console.nextLine().trim();
        return input.equalsIgnoreCase("y");
    }

    public void displayReservations(List<Reservation> reservations) {
        for (Reservation r : reservations) {
            System.out.printf("ID: %d, %s - %s%n", r.getId(), r.getStartDate(), r.getEndDate());
        }
    }

    public void displayReservationSummary(Reservation r) {
        displayHeader("Summary");
        System.out.printf("Start: %s%n", r.getStartDate());
        System.out.printf("End: %s%n", r.getEndDate());
        System.out.printf("Total: $%s%n", r.getTotal());
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

}
