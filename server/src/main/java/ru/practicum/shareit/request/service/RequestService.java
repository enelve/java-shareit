package ru.practicum.shareit.request.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestDTO;
import ru.practicum.shareit.request.entity.Request;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

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


    public Request create(@NotNull RequestDTO requestDto, Long userId) {
        Request request = RequestMapper.toRequest(requestDto)
                .setUser(userService.getById(userId))
                .setCreated(LocalDateTime.now());
        return requestRepository.save(request);
    }

    public List<RequestDTO> getByUserId(Long userId) {
        return requestRepository.findByUserOrderByCreatedDesc(getUser(userId)).stream()
                .map(RequestMapper::toDto)
                .map(requestDTO -> requestDTO.setItems(itemService.getByRequestId(requestDTO.getId())))
                .toList();
    }

    public RequestDTO getByRequestId(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("NotFoundException"));
        List<Item> items = itemService.getByRequestId(requestId);

        return RequestMapper.toDto(request).setItems(items);
    }

    public List<RequestDTO> getAll(Long userId) {
        return requestRepository.findAllByUserNot(getUser(userId)).stream()
                .map(RequestMapper::toDto)
                .map(requestDTO -> requestDTO.setItems(itemService.getByRequestId(requestDTO.getId())))
                .toList();
    }

    private User getUser(Long userId) {
        return userService.getById(userId);
    }

}
