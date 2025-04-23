package com.dwmyhouse.data;

import com.dwmyhouse.models.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationRepositoryTest {

    ReservationRepository repository;
    Path directory;

    @BeforeEach
    void setup() throws URISyntaxException {
        String directory = Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("test-data/test-reservations")).toURI()
        ).toString();

        repository = new ReservationRepository(directory);
    }

    @Test
    void shouldReadValidReservations() {
        List<Reservation> result = repository.findByHost("abc123");
        assertEquals(2, result.size());
        assertEquals("guest-1", result.get(0).getGuestId());
    }

    @Test
    void shouldReturnEmptyListForUnknownHost() {
        List<Reservation> result = repository.findByHost("not-a-real-host");
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldIgnoreInvalidRows() {
        List<Reservation> result = repository.findByHost("abc123");
        assertEquals(2,result.size());
    }

    @Test
    void shouldDeserializeValidLine() throws Exception {
        String line = "4,2025-05-01,2025-05-04,guest-3,600.00";
        Reservation r = repository.deserialize(line, "host-xyz");
        assertNotNull(r);
        assertEquals("guest-3", r.getGuestId());
    }

    @Test
    void shouldReturnNullForInvalidLine() {
        String line = "invalid,data,line,here";
        Reservation r = repository.deserialize(line,"host-abc");
        assertNull(r);
    }

    @Test
    void shouldSerializeReservationCorrectly() {
        Reservation r = new Reservation(5,
                LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 6, 4),
                "guest-99",
                new BigDecimal("720.00"));

        String line = repository.serialize(r);
        assertEquals("5,2025-06-01,2025-06-04,guest-99,720.00", line);
    }
}
