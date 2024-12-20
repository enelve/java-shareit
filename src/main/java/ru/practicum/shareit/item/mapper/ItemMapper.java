package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.ItemDataDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static Item dtoItemUpdate(ItemDto itemDto, Item item, User user) {
        return new Item(itemDto.getId() != null ? itemDto.getId() : item.getId(),
                itemDto.getName() != null ? itemDto.getName() : item.getName(),
                itemDto.getDescription() != null ? itemDto.getDescription() : item.getDescription(),
                itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getAvailable(), user);
    }

    public static Item toItem(ItemDto itemDto, User user) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), user);
    }

    public static ItemDataDto itemToDataDtoNoBooking(Item item, List<Comment> comment) {
        return new ItemDataDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), null, null, comment, item.getUser());
    }

    public static ItemDataDto itemToDataDto(List<Booking> booking, List<Booking> bookingNext, List<Comment> comment) {
        return new ItemDataDto(booking.get(0).getItem().getId(), booking.get(0).getItem().getName(),
                booking.get(0).getItem().getDescription(), booking.get(0).getItem().getAvailable(),
                BookingMapper.toDto(booking.get(0)),
                bookingNext.size() > 1 ? BookingMapper.toDto(bookingNext.get(bookingNext.size() - 1)) : null,
                comment, booking.get(0).getItem().getUser());
    }

}
