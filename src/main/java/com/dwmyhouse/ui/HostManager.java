package com.dwmyhouse.ui;

import com.dwmyhouse.domain.HostService;
import com.dwmyhouse.models.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class HostManager {

    private final HostService hostService;
    private final View view;

    @Autowired
    public HostManager(HostService hostService, View view) {
        this.hostService = hostService;
        this.view = view;
    }

    public void manageHosts() {
        while (true) {
            view.displayHostMenu();
            String choice = view.readMenuSelection("Select [0 - 4]: ");

            switch (choice) {
                case "1":
                    viewAllHosts();
                    break;
                case "2":
                    addHost();
                    break;
                case "3":
                    updateHost();
                    break;
                case "4":
                    deleteHost();
                    break;
                case "0":
                    return;
                default:
                    view.displayMessage("Invalid selection. Try again.");
            }
        }
    }

    private void viewAllHosts() {
        List<Host> hosts = hostService.findAll();
        view.displayHostTable(hosts);
    }

    private void addHost() {
        System.out.println("Inside addHost()"); //debug
        view.displayHeader("Add Host");
        Host newHost = view.readHostInfo(null);

        System.out.println("Validating host: " + newHost.getEmail()); //debug

        boolean duplicate = hostService.findAll().stream()
                .anyMatch(h -> h.getEmail().equalsIgnoreCase(newHost.getEmail().trim()));

        List<String> errors = validateHost(newHost);

        if (duplicate) {
            errors.add("A host with this email already exists. Use different email.");
        }

        System.out.println("Errors found: " + errors.size()); // debug

        if (!errors.isEmpty()) {
            view.displayHeader("Validation Failed");
            errors.forEach(view::displayMessage);
            return;
        }

        boolean added = hostService.addHost(newHost);
        System.out.println("addHost() returned: " + added); //debug line

        if (added) {
            view.displayMessage("Host added successfully.");
        } else {
            view.displayMessage("Failed to add host.");
        }
    }

    private void updateHost() {
        view.displayHeader("Update Host");
        String email = view.readValidEmail("Host Email to Update: ");
        Host existing = hostService.getHostByEmail(email);

        if (existing == null) {
            view.displayMessage("Host not found.");
            return;
        }

        Host updated = view.readHostInfo(existing);

        List<String> errors = validateHost(updated);

        if (!updated.getEmail().equalsIgnoreCase(existing.getEmail())) {
            // Ensure email is still unique
            boolean duplicate = hostService.findAll().stream()
                    .anyMatch(h -> h.getEmail().equalsIgnoreCase(updated.getEmail()));
            if (duplicate) {
                errors.add("A host with this email already exists.");
            }
        }

        if (!errors.isEmpty()) {
            view.displayHeader("Validation Failed");
            errors.forEach(view::displayMessage);
            return;
        }

        boolean result = hostService.updateHost(updated);
        if (result) {
            view.displayMessage("Host updated successfully.");
            view.displayHostSummary(updated);
        } else {
            view.displayMessage("Host update failed.");
        }

    }

    private void deleteHost() {
        view.displayHeader("Delete Host");
        String id = view.readRequiredString("Host ID to delete: ");
        if (hostService.deleteHost(id)) {
            view.displayMessage("Host deleted successfully.");
        } else {
            view.displayMessage("Delete failed.");
        }
    }

    private List<String> validateHost(Host host) {
        List<String> errors = new ArrayList<>();

        if (host.getEmail() == null || !host.getEmail().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            errors.add("Invalid email format.");
        }

        if (host.getCity() == null || host.getCity().isBlank()) {
            errors.add("City is required.");
        }

        if (host.getState() == null || host.getState().isBlank()) {
            errors.add("State is required.");
        }

        if (host.getStandardRate() == null || host.getStandardRate().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Standard rate must be a positive number.");
        }

        if (host.getWeekendsRate() == null || host.getWeekendsRate().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Weekend rate must be a positive number.");
        }

        return errors;
    }

}
