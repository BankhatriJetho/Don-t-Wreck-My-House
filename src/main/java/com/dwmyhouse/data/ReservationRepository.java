package com.dwmyhouse.data;

import com.dwmyhouse.models.Reservation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for handling reservation files per host
 */
@Repository
public class ReservationRepository {

    private static final String HEADER = "id,start_date,end_date,guest_id,total";
    private final Path reservationsDir;

    public ReservationRepository(@Value("${reservation.dir.path:./data/reservations}")String reservationsDir) {
        this.reservationsDir = Path.of(reservationsDir);
    }

    /**
     * Loads all the reservations for the given host ID from its corresponding file.
     * Skips invalid lines
     * @param hostId the host's ID
     * @return list of all reservations for that host
     */
    public List<Reservation> findByHost(String hostId) {
        List<Reservation> result = new ArrayList<>();
        Path filePath = reservationsDir.resolve(hostId + ".csv");

        if (!filePath.toFile().exists()) {
            return result;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            boolean isFirst = true;
            while ((line = reader.readLine()) != null) {
                if (isFirst && line.equalsIgnoreCase(HEADER)) {
                    isFirst = false;
                    continue;
                }
                Reservation r = deserialize(line, hostId);
                if (r != null) {
                    result.add(r);
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading reservation file for host: " + e.getMessage());
        }

        return result;
    }

    /**
     * Adds a new reservation for the given host.
     * Automatically assigns the next available ID.
     * @param reservation the reservation to add
     * @param hostId the host's ID
     * @return true if successful, false otherwise
     */
    public boolean add(Reservation reservation, String hostId) {
        List<Reservation> existing = findByHost(hostId);
        reservation.setId(generateGlobalId());
        existing.add(reservation);
        return writeAll(existing, hostId);
    }

    /**
     * Updates an existing reservation.
     * Only works if the reservation ID matches.
     * @param updated the updated reservation
     * @param hostId the host's ID
     * @return true if update succeeded, false otherwise
     */
    public boolean update(Reservation updated, String hostId) {
        List<Reservation> reservations = findByHost(hostId);
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).getId() == updated.getId()) {
                reservations.set(i, updated);
                return writeAll(reservations, hostId);
            }
        }
        return false;
    }

    /**
     * Deletes a reservation by ID for a specific host.
     * @param reservationId the reservation ID
     * @param hostId the host's ID
     * @return true if successfully deleted, false otherwise
     */
    public boolean delete(int reservationId, String hostId) {
        List<Reservation> reservations = findByHost(hostId);
        boolean removed = reservations.removeIf(r -> r.getId() == reservationId);
        return removed && writeAll(reservations, hostId);
    }

    /**
     * Converts a line of CSV into a Reservation object.
     * Returns a Reservation or null if line is invalid
     */
    Reservation deserialize(String line, String hostId) {
        String[] tokens = line.split(",", -1);
        if (tokens.length != 5) return null;

        try {
            Reservation r = new Reservation();
            r.setId(Integer.parseInt(tokens[0]));
            r.setStartDate(LocalDate.parse(tokens[1]));
            r.setEndDate(LocalDate.parse(tokens[2]));
            r.setGuestId(tokens[3]);
            r.setTotal(new BigDecimal(tokens[4]));
            r.setHostId(hostId);
            return r;
        } catch (Exception e) {
            System.out.println("Skipping bad line: " + line + " due to " + e.getMessage());
            return null;
        }
    }

    /**
     * Used for testing/debugging
     * Converts a Reservation object into a CSV line format
     * @param r reservation
     * @return comma-separated string
     */
    public String serialize(Reservation r) {
        return String.format("%s,%s,%s,%s,%s",
                r.getId(),
                r.getStartDate(),
                r.getEndDate(),
                r.getGuestId(),
                r.getTotal()
        );
    }

    /**
     * Writes all reservations for a host back to the CSV file.
     * @param reservations list of reservations
     * @param hostId the host's ID
     * @return true if file write was successful
     */
    private boolean writeAll(List<Reservation> reservations, String hostId) {
        Path filePath = reservationsDir.resolve(hostId + ".csv");
        try (PrintWriter writer = new PrintWriter(filePath.toFile())) {
            writer.println(HEADER);
            for (Reservation r : reservations) {
                writer.printf("%d,%s,%s,%s,%s%n",
                        r.getId(),
                        r.getStartDate(),
                        r.getEndDate(),
                        r.getGuestId(),
                        r.getTotal().toPlainString());
            }
            return true;
        } catch (Exception e) {
            System.out.println("Error writing reservation file for host: " + e.getMessage());
        }
        return false;
    }

    /**
     * Scans all reservation files and returns the next available global reservation ID
     */
    int generateGlobalId() {
        int maxId = 0;
        try {
            if (reservationsDir.toFile().exists()) {
                for(var file : reservationsDir.toFile().listFiles((dir,name) -> name.endsWith(".csv"))) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        boolean isFirst = true;
                        while ((line = reader.readLine()) != null) {
                            if(isFirst && line.equalsIgnoreCase(HEADER)) {
                                isFirst = false;
                                continue;
                            }
                            String[] tokens = line.split(",", -1);
                            if(tokens.length == 5) {
                                try {
                                    int id = Integer.parseInt(tokens[0]);
                                    if (id > maxId) {
                                        maxId = id;
                                    }
                                } catch (NumberFormatException  ignored) {

                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error generating global reservation ID: " + e.getMessage());
        }
        return maxId + 1;
    }

}
