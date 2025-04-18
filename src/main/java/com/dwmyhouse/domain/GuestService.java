package com.dwmyhouse.domain;

import com.dwmyhouse.data.GuestRepository;
import com.dwmyhouse.models.Guest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Provides guest-related business operations
 */
@Service
public class GuestService {

    private final GuestRepository repository;

    @Autowired
    public GuestService(GuestRepository repository) {
        this.repository = repository;
    }

    /**
     * Finds a guest by email.
     * @param email the guest's email
     * @return Guest or null if not found
     */
    public Guest getGuestByEmail(String email) {
        return repository.findByEmail(email);
    }

    /**
     * Finds guest by id
     * @param id the guest's id
     * @return Guest or null if not found
     */
    public Guest getGuestById(String id) {
        return repository.findById(id);
    }

    public List<Guest> findAll() {
        return repository.findAll();
    }

    public boolean addGuest(Guest guest) {
        return repository.add(guest);
    }

    public boolean updateGuest(Guest guest) {
        return repository.update(guest);
    }

    public boolean deleteGuest(String guestId) {
        return repository.delete(guestId);
    }
}
