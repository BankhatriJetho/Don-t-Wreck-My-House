package com.dwmyhouse.models;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a host who offers a rental location for reservations.
 * Each host has exactly one location.
 */
public class Host {

    /**
     * Unique ID of the host(UUID format).
     */
    private String id;

    /**
     * Host's last name
     */
    private String lastName;

    /**
     * Host's email address
     */
    private String email;

    /**
     * Host's phone number
     */
    private String phone;

    /**
     * Host's street address
     */
    private String address;

    /**
     * Host's city
     */
    private String city;

    /**
     * Host's US state
     */
    private String state;

    /**
     * Host's zip code
     */
    private String postalCode;

    /**
     * Standard nightly rate
     */
    private BigDecimal standardRate;

    /**
     * Weekend nightly rate
     */
    private BigDecimal weekendsRate;

    /**
     * Constructor
     */
    public Host() {
    }

    /**
     * Parametrized Constructor
     */
    public Host(String id, String lastName, String email, String phone,
                String address, String city, String state, String postalCode,
                BigDecimal standardRate, BigDecimal weekendsRate) {
        this.id = id;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.standardRate = standardRate;
        this.weekendsRate = weekendsRate;
    }

    /**
     * Getters and Setters
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public BigDecimal getStandardRate() {
        return standardRate;
    }

    public void setStandardRate(BigDecimal standardRate) {
        this.standardRate = standardRate;
    }

    public BigDecimal getWeekendsRate() {
        return weekendsRate;
    }

    public void setWeekendsRate(BigDecimal weekendsRate) {
        this.weekendsRate = weekendsRate;
    }

    @Override
    public String toString() {
        return String.format("%s: %s, %s", lastName, city, state);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if (!(o instanceof Host host))
            return false;
        return Objects.equals(id, host.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
