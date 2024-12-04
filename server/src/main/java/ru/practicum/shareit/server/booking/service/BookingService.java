package ru.practicum.shareit.server.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingStatus;
import ru.practicum.shareit.server.booking.entity.Booking;
import ru.practicum.shareit.server.booking.mapper.BookingMapper;
import ru.practicum.shareit.server.booking.repository.BookingRepository;
import ru.practicum.shareit.server.exception.EmptyResult;
import ru.practicum.shareit.server.exception.ItemNotValidException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.UnknownBookingState;
import ru.practicum.shareit.server.item.entity.Item;
import ru.practicum.shareit.server.item.repository.ItemRepository;
import ru.practicum.shareit.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    public Booking create(BookingDto bookingDto, Long id) {
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("NotFoundException"));

        Booking booking = BookingMapper.toBooking(bookingDto, userService.getById(id), item);
        if (Boolean.FALSE.equals(item.getAvailable() || booking.getEnd() == null || booking.getStart() == null ||
                booking.getEnd().isBefore(booking.getStart())) || booking.getStart().equals(booking.getEnd())
        ) {
            throw new ItemNotValidException("ItemNotValidException");
        } else if (item.getUser().getId().equals(id)) {
            throw new NotFoundException("NotFoundException");
        } else {
            return bookingRepository.save(booking);
        }
    }

    public Booking update(Long bookingId, Long userId, Boolean approved) {
        Booking bookingUpdate = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("NotFoundException"));
        Long userBookingOwner = bookingUpdate.getItem().getUser().getId();
        if (userBookingOwner.equals(userId) && bookingUpdate.getStatus() == BookingStatus.WAITING) {
            if (Boolean.TRUE.equals(approved)) {
                bookingUpdate.setStatus(BookingStatus.APPROVED);
            } else {
                bookingUpdate.setStatus(BookingStatus.REJECTED);
            }
            return bookingRepository.save(bookingUpdate);
        } else {
            throw new ItemNotValidException("ItemNotValidException");
        }
    }

    @Transactional(readOnly = true)
    public Collection<Booking> getAllBookingByUser(Long id, BookingStatus state) {
        LocalDateTime time = LocalDateTime.now();
        Collection<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(id);
        if (bookings.isEmpty()) {
            throw new EmptyResult("EmptyResult");
        } else {
            return getBookings(state, bookings, time);
        }
    }

    @Transactional(readOnly = true)
    public Booking getBookingByUser(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("NotFoundException"));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getUser().getId().equals(userId)) {
            return booking;
        } else {
            throw new NotFoundException("NotFoundException");
        }
    }

    @Transactional(readOnly = true)
    public Collection<Booking> getAllBookingItemByUser(Long id, BookingStatus state) {
        LocalDateTime time = LocalDateTime.now();
        Collection<Booking> collectionBooking = bookingRepository.findByItemUserIdOrderByStartDesc(id);
        if (collectionBooking.isEmpty()) {
            throw new EmptyResult("EmptyResult");
        } else {
            return getBookings(state, collectionBooking, time);
        }

    }

    private Collection<Booking> getBookings(BookingStatus state, Collection<Booking> booking, LocalDateTime time) {
        return switch (state) {
            case ALL -> booking;
            case REJECTED -> booking.stream().filter(p -> p.getStatus() == BookingStatus.REJECTED).toList();
            case WAITING -> booking.stream().filter(p -> p.getStatus() == BookingStatus.WAITING).toList();
            case CURRENT ->
                    booking.stream().filter(p -> time.isAfter(p.getStart()) && time.isBefore(p.getEnd())).toList();
            case PAST -> booking.stream().filter(p -> time.isAfter(p.getEnd())).toList();
            case FUTURE -> booking.stream().filter(p -> time.isBefore(p.getStart())).toList();
            default -> throw new UnknownBookingState("UnknownBookingState");
        };
    }
}
