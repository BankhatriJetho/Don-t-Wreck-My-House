package com.dwmyhouse.data;

import com.dwmyhouse.models.Guest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GuestJsonRepository {

    private final File file;
    private final ObjectMapper mapper = new ObjectMapper();

    public GuestJsonRepository(@Value("${guest.json.path:./data/guests.json}") String filePath) {
        this.file = new File(filePath);
    }

    public List<Guest> findAll() {
        try {
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return mapper.readValue(file, new TypeReference<>() {});
        } catch (Exception e) {
            System.out.println("Error reading guests.json: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Guest findById(String id) {
        return findAll().stream()
                .filter(g -> g.getGuestId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Guest findByEmail(String email) {
        return findAll().stream()
                .filter(g -> g.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    public boolean add(Guest guest) {
        List<Guest> guests = findAll();
        guests.add(guest);
        return writeAll(guests);
    }

    public boolean update(Guest updatedGuest) {
        List<Guest> guests = findAll();
        for (int i = 0; i < guests.size(); i++) {
            if (guests.get(i).getGuestId().equals(updatedGuest.getGuestId())) {
                guests.set(i, updatedGuest);
                return writeAll(guests);
            }
        }
        return false;
    }

    public boolean delete(String guestId) {
        List<Guest> guests = findAll();
        boolean removed = guests.removeIf(g -> g.getGuestId().equals(guestId));
        return removed && writeAll(guests);
    }

    private boolean writeAll(List<Guest> guests) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, guests);
            return true;
        } catch (Exception e) {
            System.out.println("Error writing guests.json: " + e.getMessage());
            return false;
        }
    }
}
