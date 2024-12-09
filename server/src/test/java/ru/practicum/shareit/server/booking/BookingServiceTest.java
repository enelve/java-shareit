package ru.practicum.shareit.server.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingStatus;
import ru.practicum.shareit.server.booking.entity.Booking;
import ru.practicum.shareit.server.booking.repository.BookingRepository;
import ru.practicum.shareit.server.booking.service.BookingService;
import ru.practicum.shareit.server.exception.EmptyResult;
import ru.practicum.shareit.server.exception.ItemNotValidException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.UnknownBookingState;
import ru.practicum.shareit.server.item.entity.Item;
import ru.practicum.shareit.server.item.repository.ItemRepository;
import ru.practicum.shareit.server.user.entity.User;
import ru.practicum.shareit.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    private final User user = new User(
            1L,
            "name",
            "email@email"
    );
    private final Item item = new Item(
            1L,
            "перчатки",
            "резиновые",
            true,
            null,
            user
    );
    private final BookingDto bookingDto = new BookingDto(
            1L,
            LocalDateTime.of(2024, 10, 10, 10, 10, 0),
            LocalDateTime.of(2024, 12, 10, 10, 10, 0),
            item.getId()
    );
    private final Booking booking = new Booking(
            1L,
            LocalDateTime.of(2024, 10, 10, 10, 10, 0),
            LocalDateTime.of(2024, 12, 10, 10, 10, 0),
            item,
            user,
            BookingStatus.REJECTED
    );
    private final Booking bookingWaiting = new Booking(
            2L,
            LocalDateTime.of(2024, 10, 10, 10, 10, 0),
            LocalDateTime.of(2024, 12, 10, 10, 10, 0),
            item,
            user,
            BookingStatus.WAITING
    );
    private final Item itemAvailableFalse = new Item(
            1L,
            "перчатки2",
            "резиновые2",
            false,
            null,
            user
    );
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserService userService;
    BookingService bookingService;

    @BeforeEach
    void init() {
        bookingService = new BookingService(bookingRepository, itemRepository, userService);
    }

    @Test
    void create_shouldThrowExceptionWhenCreateWithWrongUserId() {
        when(userService.getById(anyLong()))
                .thenThrow(NotFoundException.class);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingDto, 999L));
    }

    @Test
    void create_shouldThrowExceptionWhenCreateWithWrongItemId() {
        when(itemRepository.findById(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingDto, 999L));
    }

    @Test
    void create_shouldThrowExceptionWhenItemAvailableFalse() {
        BookingDto bookingDtoFalse = new BookingDto(
                2L,
                LocalDateTime.of(2024, 10, 10, 10, 10, 0),
                LocalDateTime.of(2024, 12, 10, 10, 10, 0),
                itemAvailableFalse.getId()
        );
        when(userService.getById(anyLong()))
                .thenReturn(user);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemAvailableFalse));

        assertThrows(ItemNotValidException.class,
                () -> bookingService.create(bookingDtoFalse, 999L));
    }

    @Test
    void create_shouldThrowExceptionWhenItemBookingByHimself() {
        when(userService.getById(anyLong()))
                .thenReturn(user);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingDto, user.getId()));
    }

    @Test
    void update_shouldThrowExceptionWhenUpdateWrongId() {
        assertThrows(NotFoundException.class,
                () -> bookingService.update(bookingDto.getId(), user.getId(), true));
    }

    @Test
    void update_shouldThrowExceptionWhenUpdateStatusRejected() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ItemNotValidException.class,
                () -> bookingService.update(bookingDto.getId(), user.getId(), true));
    }

    @Test
    void update_shouldThrowExceptionIfNotOwnerUpdatingStatus() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.update(bookingWaiting.getId(), 2L, true));
    }

    @Test
    void getBookingByUser_shouldReturnBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Booking returnedBooking = bookingService.getBookingByUser(booking.getId(), user.getId());

        assertEquals(returnedBooking.getStart(), booking.getStart());
        assertEquals(returnedBooking.getEnd(), booking.getEnd());
    }

    @Test
    void getAllBookingItemByUser_shouldReturnBookingsIfStateIsCurrentAndGetBookingsByOwner() {
        Booking bookingCurrent = new Booking(
                3L,
                LocalDateTime.of(2023, 10, 10, 10, 10, 0),
                LocalDateTime.of(2024, 12, 10, 10, 10, 0),
                item,
                user,
                BookingStatus.REJECTED
        );
        when(bookingRepository.findByItemUserIdOrderByStartDesc(any()))
                .thenReturn(List.of(bookingCurrent));

        Collection<Booking> bookings = bookingService.getAllBookingItemByUser(user.getId(),
                BookingStatus.CURRENT);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getAllBookingItemByUser_shouldReturnBookingsIfStateIsRejected() {
        Booking bookingCurrent = new Booking(
                3L,
                LocalDateTime.of(2023, 10, 10, 10, 10, 0),
                LocalDateTime.of(2024, 12, 10, 10, 10, 0),
                item,
                user,
                BookingStatus.REJECTED
        );
        when(bookingRepository.findByItemUserIdOrderByStartDesc(any()))
                .thenReturn(List.of(bookingCurrent));

        Collection<Booking> bookings = bookingService.getAllBookingItemByUser(user.getId(), BookingStatus.REJECTED);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getAllBookingItemByUser_shouldReturnBookingsIfStateIsWaiting() {
        Booking bookingCurrent = new Booking(
                3L,
                LocalDateTime.of(2023, 10, 10, 10, 10, 0),
                LocalDateTime.of(2024, 12, 10, 10, 10, 0),
                item,
                user,
                BookingStatus.WAITING
        );
        when(bookingRepository.findByItemUserIdOrderByStartDesc(any()))
                .thenReturn(List.of(bookingCurrent));

        Collection<Booking> bookings = bookingService.getAllBookingItemByUser(user.getId(), BookingStatus.WAITING);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getAllBookingItemByUser_shouldReturnBookingsIfStateIsAll() {
        Booking bookingCurrent = new Booking(3L, LocalDateTime.of(2023, 10, 10, 10, 10, 0), LocalDateTime.of(2024, 12, 10, 10, 10, 0), item, user, BookingStatus.WAITING);
        when(bookingRepository.findByItemUserIdOrderByStartDesc(any()))
                .thenReturn(List.of(bookingCurrent));

        Collection<Booking> bookings = bookingService.getAllBookingItemByUser(user.getId(), BookingStatus.ALL);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getAllBookingItemByUser_shouldThrowExceptionWhenStateIsUnsupported() {
        Booking bookingCurrent = new Booking(3L, LocalDateTime.of(2023, 10, 10, 10, 10, 0), LocalDateTime.of(2024, 12, 10, 10, 10, 0), item, user, BookingStatus.WAITING);
        when(bookingRepository.findByItemUserIdOrderByStartDesc(any()))
                .thenReturn(List.of(bookingCurrent));

        assertThrows(UnknownBookingState.class,
                () -> bookingService.getAllBookingItemByUser(user.getId(),
                        BookingStatus.UNKNOWN));
    }

    @Test
    void getAllBookingByUser_shouldThrowExceptionWhenNoBookings() {
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong()))
                .thenReturn(List.of());

        assertThrows(EmptyResult.class,
                () -> bookingService.getAllBookingByUser(1L, BookingStatus.ALL));
    }

    @Test
    void getAllBookingByUser_shouldReturnBookings() {
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong()))
                .thenReturn(List.of(booking));

        assertFalse(bookingService.getAllBookingByUser(1L, BookingStatus.ALL).isEmpty());
    }

}
