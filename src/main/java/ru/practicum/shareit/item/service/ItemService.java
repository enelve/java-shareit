package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotValidException;
import ru.practicum.shareit.exception.ItemOwnerMismatchException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    public ItemService(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    public Item create(ItemDto itemDto, Long id) {
        userService.getById(id);
        validateItem(itemDto);
        return itemRepository.create(itemDto, id);
    }

    public Item update(ItemDto item, Long id, Long userId) {
        userService.getById(userId);
        Item oldItem = getItemById(id, userId);
        if (oldItem.getUserId().compareTo(userId) != 0)
            throw new ItemOwnerMismatchException("ItemOwnerMismatchException");
        Item itemUpdate = ItemMapper.dtoItemUpdate(item, oldItem, userId);
        itemUpdate.setId(id);
        return itemRepository.update(itemUpdate, id, userId);

    }

    public Item getItemById(Long id, Long userId) {
        return itemRepository.getItemById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Item %s not found", id)));
    }

    public Collection<Item> getItemBySearch(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.getAllItems().stream().filter(item ->
                item.getDescription().toUpperCase().contains(text.toUpperCase()) ||
                        item.getName().toUpperCase().contains(text.toUpperCase())).filter(Item::getAvailable).toList();
    }


    public Collection<Item> getItemByUser(Long userId) {
        userService.getById(userId);
        return itemRepository.getAllItems().stream().filter(item -> item.getUserId().compareTo(userId) == 0)
                .filter(Item::getAvailable).toList();
    }

    private void validateItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank() ||
                itemDto.getDescription() == null || itemDto.getDescription().isBlank() ||
                itemDto.getAvailable() == null) {
            throw new ItemNotValidException(String.format("ItemNotValidException: %s", itemDto));
        }
    }
}
