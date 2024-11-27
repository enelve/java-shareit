package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.entity.Booking;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long id);

    List<Booking> findByBookerIdAndItemId(Long userId, Long itemId);

    List<Booking> findByItemUserIdOrderByStartDesc(Long id);

    List<Booking> findByItemUserIdOrderByEndDesc(Long id);

    List<Booking> findByItemIdOrderByEndDesc(Long itemId);

}
