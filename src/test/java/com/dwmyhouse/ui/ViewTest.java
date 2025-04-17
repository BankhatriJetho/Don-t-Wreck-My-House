package com.dwmyhouse.ui;

import com.dwmyhouse.models.Reservation;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class ViewTest {

    @Test
    void shouldReadMenuSelection() {
        String input = "3\n";
        View view = buildViewWithInput(input);
        String selection = view.readMenuSelection("Choose option: ");
        assertEquals("3",selection);
    }

    @Test
    void shouldReadRequiredString() {
        String input = "\n\nHello World\n";
        View view = buildViewWithInput(input);
        String result = view.readRequiredString("Enter text:");
        assertEquals("Hello World", result);
    }

    @Test
    void shouldReadDateWithoutDefault() {
        String input = "2025-06-01\n";
        View view = buildViewWithInput(input);
        LocalDate date = view.readDate("Enter date:");
        assertEquals(LocalDate.of(2025, 6, 1), date);
    }

    @Test
    void shouldReadDateWithDefault() {
        String input = "\n"; // Simulate pressing Enter to accept default
        View view = buildViewWithInput(input);
        LocalDate defaultDate = LocalDate.of(2025, 1, 1);
        LocalDate date = view.readDate("Enter date:", defaultDate);
        assertEquals(defaultDate, date);
    }

    @Test
    void shouldConfirmYesInput() {
        String input = "y\n";
        View view = buildViewWithInput(input);
        assertTrue(view.confirm("Confirm?"));
    }

    @Test
    void shouldConfirmNoInput() {
        String input = "n\n";
        View view = buildViewWithInput(input);
        assertFalse(view.confirm("Confirm?"));
    }

    @Test
    void shouldDisplayMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        View view = new View(new Scanner(System.in), new PrintStream(output));
        view.displayMessage("Test passed");

        String printed = output.toString().trim();
        assertTrue(printed.contains("Test passed"));
    }

    @Test
    void shouldDisplayHeader() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        View view = new View(new Scanner(System.in), new PrintStream(output));

        view.displayHeader("== My Reservations ==");

        String printed = output.toString().trim();
        assertTrue(printed.contains("MY RESERVATIONS"), "Should contain header lines");
    }

    @Test
    void shouldDisplayReservations() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        View view = new View(new Scanner(System.in), new PrintStream(output));

        Reservation r1 = new Reservation(1,
                LocalDate.of(2025, 4, 23),
                LocalDate.of(2025, 4, 26),
                "guest-1",
                new BigDecimal("300.00"));

        Reservation r2 = new Reservation(2,
                LocalDate.of(2025, 5, 10),
                LocalDate.of(2025, 5, 12),
                "guest-2",
                new BigDecimal("200.00"));

        List<Reservation> reservations = List.of(r1, r2);

        view.displayReservations(reservations);

        String printed = output.toString();

        assertTrue(printed.contains("ID: 1"), "Should display reservation ID 1");
        assertTrue(printed.contains("ID: 2"), "Should display reservation ID 2");
        assertTrue(printed.contains("Guest ID: guest-1"), "Should display guest-1 ID");
        assertTrue(printed.contains("Guest ID: guest-2"), "Should display guest-2 ID");
    }

    @Test
    void shouldDisplayReservationSummary() {
        Reservation r = new Reservation();
        r.setStartDate(LocalDate.of(2025, 5, 1));
        r.setEndDate(LocalDate.of(2025, 5, 4));
        r.setTotal(new BigDecimal("600.00"));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        View view = new View(new Scanner(System.in), new PrintStream(output));

        view.displayReservationSummary(r);
        String printed = output.toString().trim();

        assertTrue(printed.toUpperCase().contains("SUMMARY"));
        assertTrue(printed.contains("======================================"));
        assertTrue(printed.contains("Start: 2025-05-01"));
        assertTrue(printed.contains("End: 2025-05-04"));
        assertTrue(printed.contains("Total: $600.00"));
    }


    //Helper method
    private View buildViewWithInput(String input) {
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        return new View(new Scanner(in), new PrintStream(out));
    }
}
