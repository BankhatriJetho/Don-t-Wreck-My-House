package com.dwmyhouse.domain;

import com.dwmyhouse.data.HostRepository;
import com.dwmyhouse.models.Host;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class HostServiceTest {

    HostService service;

    @BeforeEach
    void setup() {
        HostRepository repo = new HostRepository(Paths.get("data/hosts.csv"));
        service = new HostService(repo);
    }

    @Test
    void shouldFindHostByEmail() {
        Host host = service.getHostByEmail("bcharon56@storify.com");
        assertNotNull(host);
        assertEquals("Charon", host.getLastName());
    }

    @Test
    void shouldReturnNullForUnknownEmail() {
        Host host = service.getHostByEmail("nope@host.com");
        assertNull(host);
    }

    @Test
    void shouldReturnNullWhenEmailIsNull() {
        Host host = service.getHostByEmail(null);
        assertNull(host);
    }

    @Test
    void shouldReturnNullWhenEmailIsEmpty() {
        Host host = service.getHostByEmail("");
        assertNull(host);
    }
}
