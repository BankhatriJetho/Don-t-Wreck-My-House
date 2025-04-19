package com.dwmyhouse.ui;

import com.dwmyhouse.models.Guest;
import com.dwmyhouse.models.Host;
import com.dwmyhouse.models.Reservation;
import org.springframework.stereotype.Component;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * Handles all console input/output (UI Layer)
 * This class is responsible for user interaction like
 * showing menus, reading inputs, and displaying results.
 */
@Component
public class View {

    // Date format used for flexible date input like "2025-4-21"
    private static final DateTimeFormatter FLEXIBLE_DATE = DateTimeFormatter.ofPattern("yyyy-M-d");
    private final Scanner console; //For reading user input
    private final PrintStream out; //For printing output

    // Default constructor using System.in and System.out
    public View() {
        this(new Scanner(System.in), System.out);
    }

    //Constructor for injecting custom Scanner and PrintStream (used in tests)
    public View(Scanner console, PrintStream out) {
        this.console = console;
        this.out = out;
    }

    // Displays the main Menu with user options
    public void displayMenu() {
        System.out.println("\nMain Menu");
        System.out.println("============");
        System.out.println("0. Exit");
        System.out.println("1. View Reservations for Host");
        System.out.println("2. Add/Make a Reservation");
        System.out.println("3. Edit a Reservation");
        System.out.println("4. Cancel a Reservation");
        System.out.println("5. Manage a Guest");
        System.out.println("6. Manage a Host");
        System.out.println("7. View Reservations by Filter");
        System.out.println();
    }

    // Displays a centered and formatted section header
    public void displayHeader(String header) {
        //out.println("\n" + header);
        out.println("\n" + "=".repeat(40));
        out.printf("%20s%n", header.toUpperCase());
        out.println("=".repeat(40));
        //out.println("=".repeat(header.length()));
    }

    // Reads a trimmed string input for menu selection
    public String readMenuSelection(String prompt) {
        System.out.print(prompt);
        return console.nextLine().trim();
    }

    // Prompts until a non-block string is entered
    public String readRequiredString(String prompt) {
        String result;
        do {
            System.out.print(prompt);
            result = console.nextLine().trim();
        } while (result.isBlank());
        return result;
    }

    // Validates email input format and prompts until valid
    public String readValidEmail(String prompt) {
        String input;
        while (true) {
            input = readRequiredString(prompt);

            // Regex ensures proper email structure like validemail@example.com
            if(input.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                return input;
            } else {
                displayMessage("Invalid email format. Please enter a valid email address.");
            }
        }
    }

    public String readValidEmail(String label, String defaultVal) {
        while (true) {
            String input = readOptional(label, defaultVal);
            if (input.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                return input;
            } else {
                displayMessage("Invalid email format. Please try again.");
            }
        }
    }


    // Reads a date input, allows default if left empty
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

    // Reads and validates a date input
    public LocalDate readDate(String prompt) {
        LocalDate result = null;
        while (result == null) {
            String input = readRequiredString(prompt);
            try {
                result = LocalDate.parse(input, FLEXIBLE_DATE);
            } catch (DateTimeParseException ex) {
                displayMessage("Invalid date format.");
            }
        }
        return result;
    }

    // Prompts for yes/no confirmation, returns true for y or Y
    public boolean confirm(String prompt) {
        System.out.print(prompt);
        String input = console.nextLine().trim();
        return input.equalsIgnoreCase("y");
    }

    // Displays a list of reservations in a readable format
    public void displayReservations(List<Reservation> reservations) {
        if(reservations == null || reservations.isEmpty()) {
            out.println("No reservations found.");
            return;
        }
        for (Reservation r : reservations) {
            out.printf("ID: %-3d | Dates: %-10s to %-10s | Guest ID: %-5s | Total: $%-5s%n",
                    r.getId(),
                    r.getStartDate(),
                    r.getEndDate(),
                    r.getGuestId(),
                    r.getTotal());
        }
    }

    // Formats and displays a single reservation summary
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

    // Displays any generic message to user
    public void displayMessage(String message) {
        out.println(message);
    }

    //Method for displaying guest menu
    public void displayGuestMenu() {
        System.out.println("\nGuest Management");
        System.out.println("=====================");
        System.out.println("0. Back");
        System.out.println("1. View All Guests");
        System.out.println("2. Add Guest");
        System.out.println("3. Update Guest");
        System.out.println("4. Delete Guest");
        System.out.println();
    }

    //method for reading guest info
    public Guest readGuestInfo(Guest existing) {
        Guest guest = (existing != null) ? existing : new Guest();

        if (existing == null) {
            String id = readRequiredString("Guest ID: ");
            guest.setGuestId(id);
        } else {
            displayMessage("Editing guest: " + existing.getGuestId());
        }

        guest.setFirstName(readOptional("First Name", guest.getFirstName()));
        guest.setLastName(readOptional("Last Name", guest.getLastName()));
        guest.setEmail(readOptional("Email", guest.getEmail()));
        guest.setPhone(readOptional("Phone", guest.getPhone()));
        guest.setState(readOptional("State (2-letter code)", guest.getState()));

        return guest;
    }

    //method for formating list of guest when displaying the guests
    public void displayGuestTable(List<Guest> guests) {
        if (guests == null || guests.isEmpty()) {
            displayMessage("No guests found.");
            return;
        }

        System.out.println("\nGUEST LIST");
        System.out.println("=".repeat(110));
        System.out.printf("%-8s | %-30s | %-40s | %-5s | %-15s%n",
                "ID", "Name", "Email", "State", "Phone");
        System.out.println("-".repeat(110));

        for (Guest g : guests) {
            System.out.printf("%-8s | %-30s | %-40s | %-5s | %-15s%n",
                    g.getGuestId(),
                    g.getFirstName() + " " + g.getLastName(),
                    g.getEmail(),
                    g.getState(),
                    g.getPhone());
        }

        System.out.println("=".repeat(110));
    }

    //Helper method for editing guest
    //Displays original guest value when editing guest
    private String readOptional(String label, String defaultVal) {
        System.out.printf("%s (%s): ", label, defaultVal);
        String input = console.nextLine().trim();
        return input.isEmpty() ? defaultVal : input;
    }

    //Method for displaying host menu
    public void displayHostMenu() {
        System.out.println("\nHost Management");
        System.out.println("=====================");
        System.out.println("0. Back");
        System.out.println("1. View All Hosts");
        System.out.println("2. Add Host");
        System.out.println("3. Update Host");
        System.out.println("4. Delete Host");
        System.out.println();
    }

    //method for formatting host when displaying hosts
    public void displayHostTable(List<Host> hosts) {
        if (hosts == null || hosts.isEmpty()) {
            displayMessage("No hosts found.");
            return;
        }

        System.out.println("\nHOST LIST");
        System.out.println("=".repeat(150));
        System.out.printf("%-36s | %-20s | %-40s | %-20s | %-10s | %-10s%n",
                "ID", "Last Name", "Email", "City", "State", "Rate");
        System.out.println("-".repeat(150));

        for (Host h : hosts) {
            System.out.printf("%-36s | %-20s | %-40s | %-20s | %-10s | $%-10s%n",
                    h.getId(),
                    h.getLastName(),
                    h.getEmail(),
                    h.getCity(),
                    h.getState(),
                    h.getStandardRate());
        }

        System.out.println("=".repeat(150));
    }

    //method for reading host info
    public Host readHostInfo(Host existing) {
        Host host = (existing != null) ? existing : new Host();

        if (existing == null) {
            String id = UUID.randomUUID().toString();
            host.setId(id);
        } else {
            displayMessage("Editing host: " + existing.getId());
        }

        host.setLastName(readOptional("Last Name", host.getLastName()));
        host.setEmail(readValidEmail("Email", host.getEmail()));
        host.setPhone(readOptional("Phone", host.getPhone()));
        host.setAddress(readOptional("Address", host.getAddress()));
        host.setCity(readOptional("City", host.getCity()));
        host.setState(readOptional("State (2-letter)", host.getState()));
        host.setPostalCode(readOptional("Postal Code", host.getPostalCode()));

        String defaultStandard = (host.getStandardRate() != null) ? host.getStandardRate().toString() : "0";
        String defaultWeekend = (host.getWeekendsRate() != null) ? host.getWeekendsRate().toString() : "0";

        try {
            BigDecimal standard = new BigDecimal(readOptional("Standard Rate", defaultStandard));
            BigDecimal weekend = new BigDecimal(readOptional("Weekend Rate", defaultWeekend));
            host.setStandardRate(standard);
            host.setWeekendsRate(weekend);
        } catch (NumberFormatException e) {
            displayMessage("Invalid rate entered. Using previous value.");
            host.setStandardRate(BigDecimal.ZERO);
            host.setWeekendsRate(BigDecimal.ZERO);
        }

        return host;
    }

    //method to display host summary
    public void displayHostSummary(Host host) {
        displayHeader("Updated Host Summary");
        System.out.println("ID: " + host.getId());
        System.out.println("Name: " + host.getLastName());
        System.out.println("Email: " + host.getEmail());
        System.out.println("Phone: " + host.getPhone());
        System.out.println("Address: " + host.getAddress());
        System.out.println("City: " + host.getCity());
        System.out.println("State: " + host.getState());
        System.out.println("Postal Code: " + host.getPostalCode());
        System.out.println("Standard Rate: $" + host.getStandardRate());
        System.out.println("Weekend Rate: $" + host.getWeekendsRate());
    }

    public void displayReservationFilterMenu() {
        System.out.println("\nReservation Filter Menu");
        System.out.println("===============================");
        System.out.println("0. Back");
        System.out.println("1. View reservations by Guest Email");
        System.out.println("2. View reservations by Host State");
        System.out.println("3. View reservations by Host City");
        System.out.println("4. View reservations by Host Zip Code");
        System.out.println();
    }


}
