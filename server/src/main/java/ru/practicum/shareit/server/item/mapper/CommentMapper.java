package ru.practicum.shareit.server.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.booking.entity.Booking;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static Comment toComment(CommentDto commentDto, List<Booking> booking, LocalDateTime time) {
        return new Comment(commentDto.getId(), commentDto.getText(), booking.get(0).getItem(), booking.get(0).getBooker().getName(), time);
    }
}