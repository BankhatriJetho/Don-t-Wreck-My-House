package com.dwmyhouse.data;

import com.dwmyhouse.models.Host;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HostRepositoryCrudTest {

    HostRepository repository;
    Path testFile;

    @BeforeEach
    void setup(@TempDir Path tempDir) throws Exception {
        testFile = tempDir.resolve("hosts.csv");

        try (PrintWriter writer = new PrintWriter(testFile.toFile())) {
            writer.println("id,last_name,email,phone,address,city,state,postal_code,standard_rate,weekend_rate");
            writer.println("H001,Smith,smith@example.com,123-456-7890,123 Main,Dallas,TX,75001,100,150");
            writer.println("H002,Lee,lee@example.com,456-789-0123,456 Pine,Austin,TX,73301,120,170");
        }

        repository = new HostRepository(testFile.toString());
    }

    @Test
    void shouldReadAllHosts() {
        List<Host> hosts = repository.findAll();
        assertEquals(2, hosts.size());
    }

    @Test
    void shouldFindHostByEmail() {
        Host host = repository.findByEmail("lee@example.com");
        assertNotNull(host);
        assertEquals("Austin", host.getCity());
    }

    @Test
    void shouldAddHost() {
        Host newHost = new Host();
        newHost.setId("H003");
        newHost.setLastName("Johnson");
        newHost.setEmail("johnson@example.com");
        newHost.setPhone("321-654-9870");
        newHost.setAddress("789 Oak St");
        newHost.setCity("Houston");
        newHost.setState("TX");
        newHost.setPostalCode("77001");
        newHost.setStandardRate(new BigDecimal("90"));
        newHost.setWeekendsRate(new BigDecimal("130"));

        assertTrue(repository.add(newHost));
        assertEquals(3, repository.findAll().size());
    }

    @Test
    void shouldUpdateHost() {
        Host existing = repository.findByEmail("smith@example.com");
        assertNotNull(existing);

        existing.setPhone("999-999-9999");
        assertTrue(repository.update(existing));

        Host updated = repository.findByEmail("smith@example.com");
        assertEquals("999-999-9999", updated.getPhone());
    }

    @Test
    void shouldDeleteHost() {
        assertTrue(repository.delete("H002"));
        assertNull(repository.findByEmail("lee@example.com"));
    }
}
