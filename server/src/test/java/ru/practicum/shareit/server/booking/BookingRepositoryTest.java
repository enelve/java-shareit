package ru.practicum.shareit.server.booking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.server.booking.service.BookingService;
import ru.practicum.shareit.server.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingRepositoryTest {
    private final BookingService bookingService;

    @Test
    void shouldThrowExceptionIfWrongId() {
        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingByUser(999L, 1L));
    }
}
