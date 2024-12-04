package ru.practicum.shareit.server.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.request.controller.RequestController;
import ru.practicum.shareit.server.request.dto.RequestDto;
import ru.practicum.shareit.server.request.entity.Request;
import ru.practicum.shareit.server.request.service.RequestService;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.entity.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {
    private final User user = new User(1L, "user", "email@email.ru");
    private final Request request = new Request(1L, "request", user, LocalDateTime.of(2024, 2, 2, 2, 2));
    private final RequestDto requestDto = new RequestDto(1L, "request", LocalDateTime.of(2024, 2, 2, 2, 2), new UserDto(), List.of());
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @Autowired
    private MockMvc mvc;
    @MockBean
    RequestService requestService;


    @SneakyThrows
    @Test
    void createRequestTest() {
        when(requestService.create(any(), any(Long.class)))
                .thenReturn(request);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.description").value(request.getDescription()))
                .andExpect(jsonPath("$.user.id").value(request.getUser().getId()))
                .andExpect(jsonPath("$.user.name").value(request.getUser().getName()))
                .andExpect(jsonPath("$.user.email").value(request.getUser().getEmail()))
                .andExpect(jsonPath("$.created").value(request.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }

    @SneakyThrows
    @Test
    void getRequestById() {
        when(requestService.getByRequestId(any(Long.class), any(Long.class)))
                .thenReturn(requestDto);

        mvc.perform(get("/requests/1")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.created").value(requestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }

    @SneakyThrows
    @Test
    void getAllRequests() {
        when(requestService.getAll(any(Long.class)))
                .thenReturn(List.of(requestDto));

        mvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(requestDto.getId()))
                .andExpect(jsonPath("$.[0].description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.[0].created").value(requestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }

    @SneakyThrows
    @Test
    void getRequestsByUser() {
        when(requestService.getByUserId(any(Long.class)))
                .thenReturn(List.of(requestDto));

        mvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(requestDto.getId()))
                .andExpect(jsonPath("$.[0].description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.[0].created").value(requestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }

}
