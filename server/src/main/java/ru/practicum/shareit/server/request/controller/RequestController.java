package ru.practicum.shareit.server.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.request.dto.RequestDTO;
import ru.practicum.shareit.server.request.entity.Request;
import ru.practicum.shareit.server.request.service.RequestService;

import java.util.List;


@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public Request create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody @Valid RequestDTO requestDto) {
        return requestService.create(requestDto, userId);
    }

    @GetMapping
    public List<RequestDTO> getByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getByUserId(userId);
    }

    @GetMapping("/all")
    public List<RequestDTO> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getAll(userId);
    }

    @GetMapping("/{requestId}")
    public RequestDTO getByRequestId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable("requestId") Long requestId) {
        return requestService.getByRequestId(userId, requestId);
    }
}
