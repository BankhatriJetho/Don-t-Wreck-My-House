package com.dwmyhouse.data;

import com.dwmyhouse.models.Guest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for reading Guest data from guests.csv
 */

@Repository
public class GuestRepository {

    private static final String HEADER = "guest_id,first_name,last_name,email,phone,state";
    private final Path filePath;

    public GuestRepository(@Value("${guest.file.path:./data/guest.csv}") String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads all guests from the CSV file
     * Skips header and invalid/corrupt lines
     * @return list of all guests
     */
    public List<Guest> findAll() {
        List<Guest> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            boolean isFirst = true;
            while((line = reader.readLine()) != null) {
                if(isFirst && line.equalsIgnoreCase(HEADER)) {
                    isFirst = false;
                    continue;
                }
                Guest guest = deserialize(line);
                if(guest != null) {
                    result.add(guest);
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading guests.csv: " + e.getMessage());
        }
        return result;
    }

    /**
     * Finds a guest by their email address
     * @param email the guest's email
     * @return guest or null if not found
     */
    public Guest findByEmail(String email) {
        return findAll().stream()
                .filter(g -> g.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds guest by their ID
     * @param id the guest's id
     * @return guest or null if not found
     */
    public Guest findById(String id) {
        return findAll().stream()
                .filter(g -> g.getGuestId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     *Converts a line of guest CSv into a Guest object.
     * Returns null if line is invalid or malformed.
     */
    private Guest deserialize(String line) {
        String[] tokens = line.split(",", -1);
        if(tokens.length != 6 || tokens[0].isBlank()) {
            return null;
        }

        Guest guest = new Guest();
        guest.setGuestId(tokens[0]);
        guest.setFirstName(tokens[1]);
        guest.setLastName(tokens[2]);
        guest.setEmail(tokens[3]);
        guest.setPhone(tokens[4]);
        guest.setState(tokens[5]);
        return guest;
    }

}
