package com.dwmyhouse.data;

import com.dwmyhouse.models.Reservation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReservationJsonRepository {

    private final File file;
    private final ObjectMapper mapper = new ObjectMapper();

    public ReservationJsonRepository(@Value("${reservation.json.path:./data/reservations.json}") String filePath) {
        this.file = new File(filePath);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public List<Reservation> findAll() {
        try {
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return mapper.readValue(file, new TypeReference<>() {
            });
        } catch (Exception e) {
            System.out.println("Error reading reservations.json: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Reservation> findByHostId(String hostId) {
        return findAll().stream()
                .filter(r -> hostId.equals(r.getHostId()))
                .toList();
    }

    public List<Reservation> findByGuestId(String guestId) {
        return findAll().stream()
                .filter(r -> guestId.equals(r.getGuestId()))
                .toList();
    }

    public boolean add(Reservation reservation) {
        List<Reservation> all = findAll();
        reservation.setId(nextId(all));
        all.add(reservation);
        return writeAll(all);
    }

    public boolean update(Reservation updated) {
        List<Reservation> all = findAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == updated.getId()) {
                all.set(i, updated);
                return writeAll(all);
            }
        }
        return false;
    }

    public boolean delete(int id) {
        List<Reservation> all = findAll();
        boolean removed = all.removeIf(r -> r.getId() == id);
        return removed && writeAll(all);
    }

    private int nextId(List<Reservation> reservations) {
        return reservations.stream()
                .mapToInt(Reservation::getId)
                .max()
                .orElse(0) + 1;
    }

    private boolean writeAll(List<Reservation> reservations) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, reservations);
            return true;
        } catch (Exception e) {
            System.out.println("Error writing reservations.json: " + e.getMessage());
            return false;
        }
    }

}
