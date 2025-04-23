package com.dwmyhouse.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a reservation made by a guest for a host's location
 */
public class Reservation {

    /**
     * Unique reservation ID (Unique per host)
     */
    private int id;

    /**
     * Start date of the reservation
     */
    private LocalDate startDate;

    /**
     * End date of the reservation
     */
    private LocalDate endDate;

    /**
     * The guest's Unique ID
     */
    private String guestId;

    /**
     * Total cost of the reservation
     */
    private BigDecimal total;

    private String hostId;

    /**
     * Constructor
     */
    public Reservation() {
    }

    /**
     * Parametrized Constructor
     */
    public Reservation(int id, LocalDate startDate, LocalDate endDate, String guestId, BigDecimal total) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.guestId = guestId;
        this.total = total;
    }

    /**
     * Getters and Setters
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    @Override
    public String toString() {
        return String.format("ID: %d, %s to %s, Guest ID: %s, Total: $%s", id, startDate, endDate, guestId, total);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if (!(o instanceof Reservation that))
            return false;
        return id == that.id && Objects.equals(guestId, that.guestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, guestId);
    }
}
