package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.RequestDTO;
import ru.practicum.shareit.request.entity.Request;

@UtilityClass
public class RequestMapper {
    public static Request toRequest(RequestDTO requestDTO) {
        return Request.builder()
                .id(requestDTO.getId())
                .description(requestDTO.getDescription())
                .created(requestDTO.getCreated())
                .user(requestDTO.getUser())
                .build();
    }

    public static RequestDTO toDto(Request request) {
        return RequestDTO.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .user(request.getUser())
                .build();
    }
}
