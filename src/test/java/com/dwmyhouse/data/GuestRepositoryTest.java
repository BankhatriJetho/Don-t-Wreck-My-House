package com.dwmyhouse.data;

import com.dwmyhouse.models.Guest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GuestRepositoryTest {

    GuestRepository repository;

    @BeforeEach
    void setup() throws URISyntaxException {
        Path path = Paths.get(getClass().getClassLoader()
                .getResource("test-data/test-guests.csv")
                .toURI());
        repository = new GuestRepository(path);
    }

    @Test
    void shouldReadAllGuests() {
        List<Guest> result = repository.findAll();
        assertEquals(2,result.size());
    }

    @Test
    void shouldFindGuestByEmail() {
        Guest guest = repository.findByEmail("test@example.com");
        assertNotNull(guest);
        assertEquals("Testy", guest.getFirstName());
    }

    @Test
    void shouldReturnNullForUnknownEmail() {
        Guest guest = repository.findByEmail("ghost@void.com");
        assertNull(guest);
    }

    @Test
    void shouldHandleInvalidEmailFormatGracefully() {
        Guest guest = repository.findByEmail("not-an-email");
        assertNull(guest); // repo only matches by string, not format
    }

    @Test
    void shouldReturnEmptyListIfFileMissing() {
        Path fakePath = Paths.get("src/test/resources/test-data/file-doesn't-exist.csv");
        GuestRepository badRepo = new GuestRepository(fakePath);
        List<Guest> result = badRepo.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindGuestById() {
        Guest guest = repository.findById("1");
        assertNotNull(guest);
        assertEquals("Testy", guest.getFirstName());
    }

    @Test
    void shouldReturnNullForUnknownId() {
        Guest guest = repository.findById("999");
        assertNull(guest);
    }

    @Test
    void shouldDeserializeValidLine() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String line = "3,Firsty,Lasty,firsty@example.com,1231231234,TX";
        Method method = GuestRepository.class.getDeclaredMethod("deserialize", String.class);
        method.setAccessible(true);
        Guest guest = (Guest) method.invoke(repository, line);

        assertNotNull(guest);
        assertEquals("Firsty", guest.getFirstName());
    }

    @Test
    void shouldReturnNullForInvalidLineFormat() throws Exception {
        String badLine = "bad,data,line";
        Method method = GuestRepository.class.getDeclaredMethod("deserialize", String.class);
        method.setAccessible(true);
        Guest guest = (Guest) method.invoke(repository, badLine);

        assertNull(guest);
    }
}
