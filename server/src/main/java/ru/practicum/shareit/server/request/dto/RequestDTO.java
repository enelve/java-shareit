package ru.practicum.shareit.server.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.server.item.entity.Item;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
public class RequestDTO {
    private Long id;
    private String description;
    private LocalDateTime created;
    private UserDto user;
    private List<Item> items;
}