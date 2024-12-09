package ru.practicum.shareit.server.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.entity.Booking;
import ru.practicum.shareit.server.booking.repository.BookingRepository;
import ru.practicum.shareit.server.exception.ItemNotValidException;
import ru.practicum.shareit.server.exception.ItemOwnerMismatchException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDataDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.entity.Comment;
import ru.practicum.shareit.server.item.entity.Item;
import ru.practicum.shareit.server.item.mapper.CommentMapper;
import ru.practicum.shareit.server.item.mapper.ItemMapper;
import ru.practicum.shareit.server.item.repository.CommentRepository;
import ru.practicum.shareit.server.item.repository.ItemRepository;
import ru.practicum.shareit.server.user.entity.User;
import ru.practicum.shareit.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public Item create(ItemDto itemDto, Long userId) {
        checkItem(itemDto);
        User user = userService.getById(userId);
        return itemRepository.save(ItemMapper.toItem(itemDto, user));
    }

    public Item update(ItemDto item, Long id, Long userId) {
        User user = userService.getById(userId);
        if (itemRepository.findById(id).isPresent()) {
            Item itemDB = itemRepository.findById(id).get();
            if (itemDB.getUser().getId().compareTo(userId) != 0)
                throw new ItemOwnerMismatchException("ItemOwnerMismatchException");
            Item itemUpdate = ItemMapper.dtoItemUpdate(item, itemDB, user);
            itemUpdate.setId(id);
            return itemRepository.save(itemUpdate);
        } else {
            throw new NotFoundException("NotFoundException");
        }
    }

    @Transactional(readOnly = true)
    public ItemDataDto getItemById(Long id, Long userID) {
        if (itemRepository.findById(id).isPresent()) {
            Item item = itemRepository.findById(id).get();
            List<Comment> comments = commentRepository.findByItemId(id);
            LocalDateTime time = LocalDateTime.now();

            List<Booking> bookings = bookingRepository.findByItemIdOrderByEndDesc(id).stream()
                    .filter(i -> i.getStart().isBefore(time) &&
                            i.getItem().getUser().getId().equals(userID))
                    .toList();

            List<Booking> bookingNext = bookingRepository.findByItemIdOrderByEndDesc(id).stream()
                    .filter(p -> p.getEnd().isAfter(time) && p.getStart().isAfter(time) &&
                            p.getItem().getUser().getId().equals(userID))
                    .toList();

            if (!bookings.isEmpty()) {
                return ItemMapper.itemToDataDto(bookings, bookingNext, comments);
            } else {
                return ItemMapper.itemToDataDtoNoBooking(item, comments);
            }
        } else {
            throw new NotFoundException("NOT_FOUND_MESSAGE");
        }
    }

    @Transactional(readOnly = true)
    public Collection<Item> getItemBySearch(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.findAllByNameContainingIgnoreCase(text).stream().filter(Item::getAvailable).toList();
    }


    @Transactional(readOnly = true)
    public Collection<ItemDataDto> getItemByUser(Long userId) {
        userService.getById(userId);
        List<Item> items = itemRepository.findByUserId(userId);
        List<ItemDataDto> dataDtoList = new ArrayList<>();

        while (!items.isEmpty()) {
            dataDtoList.add(ItemMapper.itemToDataDtoNoBooking(items.get(0), commentRepository.findByItemId(items.get(0).getId())));
            items.remove(0);
        }

        List<ItemDataDto> bookingItems = new ArrayList<>();
        LocalDateTime time = LocalDateTime.now();

        List<Booking> booking = bookingRepository.findByItemUserIdOrderByEndDesc(userId).stream().filter(p -> p.getEnd().isBefore(time) && p.getStart().isBefore(time) && p.getItem().getUser().getId().equals(userId)).toList();

        List<Booking> bookingNext = bookingRepository.findByItemUserIdOrderByEndDesc(userId).stream().filter(p -> p.getEnd().isAfter(time) && p.getStart().isAfter(time) && p.getItem().getUser().getId().equals(userId)).toList();

        if (!booking.isEmpty() && !bookingNext.isEmpty()) {
            while (!booking.isEmpty() && !bookingNext.isEmpty()) {
                bookingItems.add(ItemMapper.itemToDataDto(booking, bookingNext, commentRepository.findByItemId(booking.get(0).getItem().getId())));
                booking.remove(0);
                bookingNext.remove(bookingNext.size() - 1);
            }
            bookingItems.addAll(dataDtoList);
            return bookingItems.stream().distinct().sorted(Comparator.comparing(ItemDataDto::getId)).toList();
        } else {
            return dataDtoList.stream().distinct().sorted(Comparator.comparing(ItemDataDto::getId)).toList();
        }
    }

    public Comment addComment(CommentDto commentDto, Long id, Long itemId) {
        LocalDateTime time = LocalDateTime.now();
        List<Booking> booking = bookingRepository.findByBookerIdAndItemId(id, itemId).stream().filter(b -> b.getStart().isBefore(time)).toList();

        if (!booking.isEmpty() && !commentDto.getText().isEmpty()) {
            Comment comment = CommentMapper.toComment(commentDto, booking, time);
            return commentRepository.save(comment);
        } else {
            throw new ItemNotValidException("ItemNotValidException");
        }
    }

    public List<Item> getByRequestId(Long requestId) {
        return itemRepository.findByRequestId(requestId);
    }

    public void checkItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank() ||
                itemDto.getDescription() == null || itemDto.getDescription().isBlank() ||
                itemDto.getAvailable() == null) {
            throw new ItemNotValidException("ItemNotValidException");
        }
    }
}
