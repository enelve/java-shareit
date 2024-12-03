package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDTO;
import ru.practicum.shareit.request.entity.Request;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;


@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
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
