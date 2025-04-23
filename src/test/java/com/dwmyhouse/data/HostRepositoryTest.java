package com.dwmyhouse.data;

import com.dwmyhouse.models.Host;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class HostRepositoryTest {

    HostRepository repository;

    @BeforeEach
    void setup() throws Exception {
        String path = Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("test-data/test-hosts.csv")).toURI()
        ).toString();
        repository = new HostRepository(path);
    }

    @Test
    void shouldReadAllHosts() {
        List<Host> result = repository.findAll();
        assertEquals(2, result.size());
    }

    @Test
    void shouldFindHostByEmail() {
        Host host = repository.findByEmail("doe@example.com");
        assertNotNull(host);
        assertEquals("Doe", host.getLastName());
    }

    @Test
    void shouldReturnNullForUnknownEmail() {
        Host host = repository.findByEmail("not@real.com");
        assertNull(host);
    }

    @Test
    void shouldReturnNullForInvalidEmailFormat() {
        Host result = repository.findByEmail("invalid-email");
        assertNull(result);
    }

    @Test
    void shouldSkipInvalidLinesInCsv() throws URISyntaxException {
        String path = Paths.get(
                Objects.requireNonNull(getClass().getClassLoader()
                .getResource("test-data/test-hosts-invalid.csv")).toURI()
        ).toString();

        HostRepository repo = new HostRepository(path);
        List<Host> result = repo.findAll();
        assertEquals(1,result.size()); //1 valid and 1 invalid row in csv
    }

    @Test
    void shouldReturnEmptyListWhenFileMissing() throws URISyntaxException {
        HostRepository repo = new HostRepository("./not-a-real-file.csv");
        List<Host> result = repo.findAll();
        assertTrue(result.isEmpty());
    }
}
