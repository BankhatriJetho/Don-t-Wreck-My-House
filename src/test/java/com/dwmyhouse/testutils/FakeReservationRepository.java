package com.dwmyhouse.testutils;

import com.dwmyhouse.data.ReservationRepository;
import com.dwmyhouse.models.Reservation;

import java.util.ArrayList;
import java.util.List;

/**
 * Fake in-memory ReservationRepository for testing domain logic.
 */
public class FakeReservationRepository extends ReservationRepository {

    private final List<Reservation> storage = new ArrayList<>();

    public FakeReservationRepository() {
        super(null); //we are not using the actual file
    }

    @Override
    public List<Reservation> findByHost(String hostId) {
        return storage.stream()
                .filter(r -> hostId.equals(r.getHostId()))
                .toList();
    }

    @Override
    public boolean add(Reservation reservation, String hostId) {
        reservation.setHostId(hostId);
        storage.add(reservation);
        return true;
    }

    @Override
    public boolean update(Reservation reservation, String hostId) {
        for(int i = 0; i < storage.size(); i++) {
            if(storage.get(i).getId() == reservation.getId()) {
                storage.set(i, reservation);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete (int reservationId, String hostId) {
        return storage.removeIf(r -> r.getId() == reservationId);
    }

    public void clearAll() {
        storage.clear();
    }
}
