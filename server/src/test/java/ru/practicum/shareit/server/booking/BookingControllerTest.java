package ru.practicum.shareit.server.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.booking.controller.BookingController;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingStatus;
import ru.practicum.shareit.server.booking.entity.Booking;
import ru.practicum.shareit.server.booking.service.BookingService;
import ru.practicum.shareit.server.item.entity.Item;
import ru.practicum.shareit.server.user.entity.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @Autowired
    private MockMvc mvc;
    @MockBean
    BookingService bookingService;

    private final User user = new User(1L, "name", "email@email");
    private final Item item = new Item(1L, "перчатки", "резиновые", true, null, user);
    private final BookingDto bookingDto = new BookingDto(1L, LocalDateTime.of(2024, 10, 10, 10, 10, 0), LocalDateTime.of(2024, 12, 10, 10, 10, 0), item.getId());
    private final Booking booking = new Booking(1L, LocalDateTime.of(2024, 10, 10, 10, 10, 0), LocalDateTime.of(2024, 12, 10, 10, 10, 0), item, user, BookingStatus.APPROVED);

    @SneakyThrows
    @Test
    void create_shouldCorrectlyCreateBooking() {
        when(bookingService.create(any(), anyLong()))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.start").value(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.end").value(booking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }

    @SneakyThrows
    @Test
    void getBookingByUser_shouldCorrectlyReturnBookingByID() {
        when(bookingService.getBookingByUser(anyLong(), anyLong()))
                .thenReturn(booking);

        mvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.start").value(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.end").value(booking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }

    @SneakyThrows
    @Test
    void getAllBookingByUser_shouldCorrectlyReturnListOfBookings() {
        when(bookingService.getAllBookingByUser(any(Long.class), any(BookingStatus.class)))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(booking.getId()))
                .andExpect(jsonPath("$.[0].start").value(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.[0].end").value(booking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }

    @SneakyThrows
    @Test
    void update_shouldCorrectlyUpdateBooking() {
        when(bookingService.update(any(Long.class), any(Long.class), any(Boolean.class)))
                .thenReturn(booking);

        mvc.perform(patch("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.start").value(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.end").value(booking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }

    @SneakyThrows
    @Test
    void getAllBookingItemByUser_shouldCorrectlyReturnListOfBookingsByOwner() {
        when(bookingService.getAllBookingItemByUser(any(Long.class), any(BookingStatus.class)))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner?from=0&size=10")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(booking.getId()))
                .andExpect(jsonPath("$.[0].start").value(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.[0].end").value(booking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }


    @SneakyThrows
    @Test
    void dtoValidationFailed() {
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(new BookingDto()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }

}
