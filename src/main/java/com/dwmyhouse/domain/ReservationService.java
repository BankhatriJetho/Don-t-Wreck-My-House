package com.dwmyhouse.domain;

import com.dwmyhouse.data.ReservationRepository;
import com.dwmyhouse.models.Host;
import com.dwmyhouse.models.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * Handles reservation business logic including validation and cost calculation
 */
@Service
public class ReservationService {

    private final ReservationRepository repository;

    @Autowired
    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    /**
     * Returns all reservation for a given host
     * @param hostId the host's ID
     * @return List of reservations
     */
    public List<Reservation> viewReservationsForHost(String hostId) {
        return repository.findByHost(hostId);
    }

    /**
     * Tries to make a reservation after validating date range and overlaps.
     * @param reservation the reservation to make
     * @param host the host (for rates and ID)
     * @return true if reservation was successful
     */
    public boolean makeReservation(Reservation reservation, Host host ) {
        if(!isValid(reservation, host))
            return false;
        reservation.setTotal(calculateTotal(reservation, host));
        return repository.add(reservation, host.getId());
    }

    /**
     * Edits a reservation after validation
     * Only dates and total can change
     * @param reservation the updated reservation
     * @param host the host
     * @return true if update was successful
     */
    public boolean editReservation(Reservation reservation, Host host) {
        if(!isValid(reservation, host)) {
            return false;
        }
        reservation.setTotal(calculateTotal(reservation, host));

        return repository.update(reservation, host.getId());
    }

    /**
     * Cancels a future reservation
     * @param reservationId the ID of the reservation
     * @param hostId the Host's ID
     * @return true if successfully cancelled
     */
    public boolean cancelReservation(int reservationId, String hostId) {
        Reservation res = repository.findByHost(hostId).stream()
                .filter(r -> r.getId() == reservationId)
                .findFirst()
                .orElse(null);

        if(res == null || res.getStartDate().isBefore(LocalDate.now())) {
            return false;
        }
        return repository.delete(reservationId, hostId);
    }

    private boolean isValid(Reservation reservation, Host host) {
        if(reservation == null || host == null) {
            return false;
        }
        if(reservation.getStartDate() == null || reservation.getEndDate() == null) {
            return false;
        }
        if(!reservation.getStartDate().isBefore(reservation.getEndDate())) {
            return false;
        }
        if(!reservation.getStartDate().isAfter(LocalDate.now())) {
            return false;
        }

        List<Reservation> existing = repository.findByHost(host.getId());
        for(Reservation r : existing) {
            if(r.getId() == reservation.getId())
                continue;
            if(datesOverlap(r, reservation))
                return false;
        }

        return true;
    }

    private boolean datesOverlap(Reservation r1, Reservation r2) {
        return !r1.getEndDate().isBefore(r2.getStartDate()) &&
                !r1.getStartDate().isAfter(r2.getEndDate());
    }

    private BigDecimal calculateTotal(Reservation reservation, Host host) {
        BigDecimal total = BigDecimal.ZERO;
        LocalDate date = reservation.getStartDate();

        while(!date.isAfter(reservation.getEndDate().minusDays(1))) {
            if(isWeekend(date)) {
                total = total.add(host.getWeekendsRate());
            } else {
                total = total.add(host.getStandardRate());
            }
            date = date.plusDays(1);
        }

        return total;
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.FRIDAY || day == DayOfWeek.SATURDAY;
    }

}
