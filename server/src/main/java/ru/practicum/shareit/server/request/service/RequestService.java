package ru.practicum.shareit.server.request.service;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.entity.Item;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.request.dto.RequestDto;
import ru.practicum.shareit.server.request.entity.Request;
import ru.practicum.shareit.server.request.mapper.RequestMapper;
import ru.practicum.shareit.server.request.repository.RequestRepository;
import ru.practicum.shareit.server.user.entity.User;
import ru.practicum.shareit.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final ItemService itemService;


    public Request create(@NotNull RequestDto requestDto, Long userId) {
        Request request = RequestMapper.toRequest(requestDto)
                .setUser(userService.getById(userId))
                .setCreated(LocalDateTime.now());
        return requestRepository.save(request);
    }

    public List<RequestDto> getByUserId(Long userId) {
        return requestRepository.findByUserOrderByCreatedDesc(getUser(userId)).stream()
                .map(RequestMapper::toDto)
                .map(requestDto -> requestDto.setItems(itemService.getByRequestId(requestDto.getId())))
                .toList();
    }

    public RequestDto getByRequestId(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("NotFoundException"));
        List<Item> items = itemService.getByRequestId(requestId);

        return RequestMapper.toDto(request).setItems(items);
    }

    public List<RequestDto> getAll(Long userId) {
        return requestRepository.findAllByUserNot(getUser(userId)).stream()
                .map(RequestMapper::toDto)
                .map(requestDto -> requestDto.setItems(itemService.getByRequestId(requestDto.getId())))
                .toList();
    }

    private User getUser(Long userId) {
        return userService.getById(userId);
    }

}
