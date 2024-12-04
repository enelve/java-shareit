package ru.practicum.shareit.server.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.server.request.dto.RequestDTO;
import ru.practicum.shareit.server.request.entity.Request;
import ru.practicum.shareit.server.user.mapper.UserMapper;

@UtilityClass
public class RequestMapper {
    public static Request toRequest(RequestDTO requestDTO) {
        return Request.builder()
                .id(requestDTO.getId())
                .description(requestDTO.getDescription())
                .created(requestDTO.getCreated())
                .user(UserMapper.toUser(requestDTO.getUser()))
                .build();
    }

    public static RequestDTO toDto(Request request) {
        return RequestDTO.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .user(UserMapper.toDto(request.getUser()))
                .build();
    }
}
