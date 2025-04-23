package com.dwmyhouse.data;

import com.dwmyhouse.models.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationRepositoryTest {

    ReservationRepository repository;

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

    @Test
    void shouldReturnNextGlobalIdAcrossFiles() throws IOException {
        Path testDir = Files.createTempDirectory("reservations");

        // Create mock reservation files
        Path file1 = testDir.resolve("hostA.csv");
        Files.write(file1, List.of(
                "id,start_date,end_date,guest_id,total",
                "1,2025-04-20,2025-04-22,G001,200",
                "2,2025-04-25,2025-04-26,G002,100"
        ));

        Path file2 = testDir.resolve("hostB.csv");
        Files.write(file2, List.of(
                "id,start_date,end_date,guest_id,total",
                "3,2025-05-01,2025-05-03,G003,300",
                "7,2025-06-01,2025-06-05,G004,500"
        ));

        // Inject custom ReservationRepository with test dir
        ReservationRepository repo = new ReservationRepository(testDir.toString());

        int globalId = repo.generateGlobalId(); // Should return 8

        assertEquals(8, globalId);
    }

    @Test
    void shouldReturnOneIfNoValidReservationFilesExist() throws IOException {
        Path testDir = Files.createTempDirectory("empty-reservations");

        // Create a file with corrupt lines
        Path badFile = testDir.resolve("corrupt.csv");
        Files.write(badFile, List.of(
                "id,start_date,end_date,guest_id,total",
                "bad,data,that,does,not,match"
        ));

        // Inject custom ReservationRepository
        ReservationRepository repo = new ReservationRepository(testDir.toString());

        int globalId = repo.generateGlobalId(); // Should return 1 safely

        assertEquals(1, globalId);
    }

}
