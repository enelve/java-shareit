package ru.practicum.shareit.server.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.booking.dto.BookingDataDto;
import ru.practicum.shareit.server.item.entity.Comment;
import ru.practicum.shareit.server.user.entity.User;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDataDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDataDto lastBooking;
    private BookingDataDto nextBooking;
    private List<Comment> comments;
    private User user;
    private Long requestId;
}
