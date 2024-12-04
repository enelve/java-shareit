package ru.practicum.shareit.server.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.booking.dto.BookingStatus;
import ru.practicum.shareit.server.item.entity.Item;
import ru.practicum.shareit.server.user.entity.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "booking", schema = "public")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date")
    private LocalDateTime start;

    @Column(name = "end_date")
    private LocalDateTime end;

    @ManyToOne
    private Item item;

    @ManyToOne
    private User booker;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status")
    private BookingStatus status;
}
