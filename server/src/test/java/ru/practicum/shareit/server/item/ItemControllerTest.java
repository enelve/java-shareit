package ru.practicum.shareit.server.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.item.controller.ItemController;
import ru.practicum.shareit.server.item.dto.ItemDataDto;
import ru.practicum.shareit.server.item.entity.Comment;
import ru.practicum.shareit.server.item.entity.Item;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.user.entity.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    private final User testUser = new User(
            1L,
            "test",
            "test@mail.ru");
    private final Item testItem = new Item(1L, "item", "item for job", true, 1L, testUser);
    private final Comment comment = new Comment(1L, "comment", testItem, testUser.getName(), LocalDateTime.of(2021, 2, 2, 2, 2));
    private final ItemDataDto testItemDataDto = new ItemDataDto(1L, "test", "item for job", true, null, null, null, testUser, null);
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @Autowired
    private MockMvc mvc;
    @MockBean
    ItemService itemService;


    @SneakyThrows
    @Test
    void shouldCorrectlyCreateItem() {
        when(itemService.create(any(), any(Long.class)))
                .thenReturn(testItem);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(testItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testItem.getId()))
                .andExpect(jsonPath("$.name").value(testItem.getName()))
                .andExpect(jsonPath("$.description").value(testItem.getDescription()))
                .andExpect(jsonPath("$.available").value(testItem.getAvailable()));
    }

    @SneakyThrows
    @Test
    void shouldReturnItemById() {
        when(itemService.getItemById(any(Long.class), any(Long.class)))
                .thenReturn(testItemDataDto);

        mvc.perform(get("/items/1")
                        .content(mapper.writeValueAsString(testItemDataDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testItemDataDto.getId()))
                .andExpect(jsonPath("$.name").value(testItemDataDto.getName()))
                .andExpect(jsonPath("$.description").value(testItemDataDto.getDescription()))
                .andExpect(jsonPath("$.available").value(testItemDataDto.getAvailable()));
    }

    @SneakyThrows
    @Test
    void shouldReturnListOfItems() {
        when(itemService.getItemByUser(any(Long.class)))
                .thenReturn(List.of(testItemDataDto));

        mvc.perform(get("/items")
                        .content(mapper.writeValueAsString(testItemDataDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(testItemDataDto.getId()))
                .andExpect(jsonPath("$.[0].name").value(testItemDataDto.getName()))
                .andExpect(jsonPath("$.[0].description").value(testItemDataDto.getDescription()))
                .andExpect(jsonPath("$.[0].available").value(testItemDataDto.getAvailable()));
    }

    @SneakyThrows
    @Test
    void shouldUpdateItem() {
        when(itemService.update(any(), any(Long.class), any(Long.class)))
                .thenReturn(testItem);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(testItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testItem.getId()))
                .andExpect(jsonPath("$.name").value(testItem.getName()))
                .andExpect(jsonPath("$.description").value(testItem.getDescription()))
                .andExpect(jsonPath("$.available").value(testItem.getAvailable()));
    }

    @SneakyThrows
    @Test
    void shouldReturnItemsList() {
        when(itemService.getItemBySearch(any(String.class)))
                .thenReturn(List.of(testItem));

        mvc.perform(get("/items/search?text=description")
                        .content(mapper.writeValueAsString(testItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(testItem.getId()))
                .andExpect(jsonPath("$.[0].name").value(testItem.getName()))
                .andExpect(jsonPath("$.[0].description").value(testItem.getDescription()))
                .andExpect(jsonPath("$.[0].available").value(testItem.getAvailable()));
    }

    @SneakyThrows
    @Test
    void shouldCreateComment() {
        when(itemService.addComment(any(), any(Long.class), any(Long.class)))
                .thenReturn(comment);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(comment.getId()))
                .andExpect(jsonPath("$.text").value(comment.getText()))
                .andExpect(jsonPath("$.authorName").value(comment.getAuthorName()))
                .andExpect(jsonPath("$.created").value(comment.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }
}
