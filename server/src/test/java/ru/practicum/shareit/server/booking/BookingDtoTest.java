package ru.practicum.shareit.server.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.server.booking.dto.BookingDataDto;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingStatus;
import ru.practicum.shareit.server.booking.entity.Booking;
import ru.practicum.shareit.server.booking.mapper.BookingMapper;
import ru.practicum.shareit.server.item.entity.Item;
import ru.practicum.shareit.server.user.entity.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@JsonTest
class BookingDtoTest {
    private final BookingDto booking = new BookingDto(1L, LocalDateTime.of(2020, 10, 10, 10, 10, 0), LocalDateTime.of(2020, 12, 10, 10, 10, 0), 1L);
    @Autowired
    private JacksonTester<BookingDto> json;

    @SneakyThrows
    @Test
    void testBookingDto() {
        JsonContent<BookingDto> result = json.write(booking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2020-10-10T10:10:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2020-12-10T10:10:00");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
    }

    @Test
    void testBookingDataDto() {
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now(), new Item(), new User(), BookingStatus.APPROVED);
        BookingDataDto bookingDataDto = BookingMapper.toDto(booking);
        assertEquals(bookingDataDto.getId(), booking.getId());
        assertEquals(bookingDataDto.getStart(), booking.getStart());
        assertEquals(bookingDataDto.getEnd(), booking.getEnd());
        assertNull(bookingDataDto.getBookerId());
    }
}
