package com.dwmyhouse.models;

import java.util.Objects;

/**
 * Represents a guest who can make reservations
 */
public class Guest {

    /**
     * Unique ID of the guest, matches guest_id in the CSV.
     */
    private String guestId;

    /**
     * Guest's First Name
     */
    private String firstName;

    /**
     * Guest's Last Name
     */
    private String lastName;

    /**
     * Guest's Email address
     */
    private String email;

    /**
     * Guest Phone number
     */
    private String phone;

    /**
     * US state where Guest is located
     */
    private String state;

    /**
     * Constructor
     */
    public Guest() {
    }

    /**
     * Parameterized Constructor
     */
    public Guest(String guestId, String firstName, String lastName, String email, String phone, String state) {
        this.guestId = guestId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.state = state;
    }

    /**
     * Getters and Setters
     */
    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s)", firstName, lastName, email);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(!(o instanceof Guest guest))
            return false;
        return Objects.equals(guestId, guest.guestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guestId);
    }

}
