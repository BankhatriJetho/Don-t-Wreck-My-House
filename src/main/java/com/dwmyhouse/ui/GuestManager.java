package com.dwmyhouse.ui;

import com.dwmyhouse.domain.GuestService;
import com.dwmyhouse.models.Guest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GuestManager {

    private final GuestService guestService;
    private final View view;

    @Autowired
    public GuestManager(GuestService guestService, View view) {
        this.guestService = guestService;
        this.view = view;
    }

    /**
     * Handles logic to manage guests operation such as create, edit, delete
     */
    public void manageGuests() {
        while (true) {
            view.displayGuestMenu();
            String choice = view.readMenuSelection("Select [0 -4]: ");

            switch (choice) {
                case "1":
                    viewAllGuests();
                    break;
                case "2":
                    addGuest();
                    break;
                case"3":
                    updateGuest();
                    break;
                case "4":
                    deleteGuest();
                    break;
                case "0":
                    return;
                default:
                    view.displayMessage("Invalid selection. Try again.");
            }
        }
    }

    //helper methods for guest operations
    private void viewAllGuests() {
        List<Guest> guests = guestService.findAll();
        view.displayGuestTable(guests);
    }

    private void addGuest() {
        view.displayHeader("Add Guest");
        Guest guest = view.readGuestInfo(null);
        if (guestService.addGuest(guest)) {
            view.displayMessage("Guest added successfully.");
        } else {
            view.displayMessage("Failed to add guest.");
        }
    }

    private void updateGuest() {
        view.displayHeader("Update Guest");
        String id = view.readRequiredString("Guest ID to update: ");
        Guest existing = guestService.getGuestById(id);
        if (existing == null) {
            view.displayMessage("Guest not found.");
            return;
        }

        Guest updated = view.readGuestInfo(existing);
        if (guestService.updateGuest(updated)) {
            view.displayMessage("Guest updated successfully.");
        } else {
            view.displayMessage("Update failed.");
        }
    }

    private void deleteGuest() {
        view.displayHeader("Delete Guest");
        String id = view.readRequiredString("Guest ID to delete: ");
        if (guestService.deleteGuest(id)) {
            view.displayMessage("Guest deleted successfully.");
        } else {
            view.displayMessage("Delete failed.");
        }
    }

}
