package com.dwmyhouse.data;

import com.dwmyhouse.models.Guest;
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

    public GuestRepository(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all guests from the CSV file
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

    private Guest deserialize(String line) {
        String[] tokens = line.split(",", -1);
        if(tokens.length != 6)
            return null;

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
