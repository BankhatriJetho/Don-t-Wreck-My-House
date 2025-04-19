package com.dwmyhouse.migration;

import com.dwmyhouse.data.*;
import com.dwmyhouse.models.Guest;
import com.dwmyhouse.models.Host;
import com.dwmyhouse.models.Reservation;
import com.dwmyhouse.ui.ReservationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataMigrationService {

    private final GuestRepository guestRepository;
    private final GuestJsonRepository guestJsonRepository;

    private final HostRepository hostRepository;
    private final HostJsonRepository hostJsonRepository;

    private final ReservationRepository reservationRepository;
    private final ReservationJsonRepository reservationJsonRepository;


    @Autowired
    public DataMigrationService(
            GuestRepository guestRepository,
            GuestJsonRepository guestJsonRepository,
            HostRepository hostRepository,
            HostJsonRepository hostJsonRepository,
            ReservationRepository reservationRepository,
            ReservationJsonRepository reservationJsonRepository
    ) {
        this.guestRepository = guestRepository;
        this.guestJsonRepository = guestJsonRepository;
        this.hostRepository = hostRepository;
        this.hostJsonRepository = hostJsonRepository;
        this.reservationRepository = reservationRepository;
        this.reservationJsonRepository = reservationJsonRepository;
    }

    public void migrateAll() {
        migrateGuests();
        migrateHosts();
        migrateReservations();
    }

    public void migrateGuests() {
        System.out.println("Migrating guests...");
        List<Guest> guests = guestRepository.findAll();
        for (Guest g : guests) {
            guestJsonRepository.add(g);
        }
        System.out.println("Guests migrated: " + guests.size());
    }

    public void migrateHosts() {
        System.out.println("Migrating hosts...");
        List<Host> hosts = hostRepository.findAll();
        for (Host h : hosts) {
            hostJsonRepository.add(h);
        }
        System.out.println("Hosts migrated: " + hosts.size());
    }

    public void migrateReservations() {
        System.out.println("Migrating reservations...");
        List<Host> hosts = hostRepository.findAll();

        int total = 0;
        for(Host h : hosts) {
            List<Reservation> hostReservations = reservationRepository.findByHost(h.getId());
            for (Reservation r : hostReservations) {
                r.setHostId(h.getId());
                reservationJsonRepository.add(r);
                total++;
            }
        }
        System.out.println("Reservations migrated: " + total);
    }

}
