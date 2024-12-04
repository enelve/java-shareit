package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.request.dto.RequestDto;
import ru.practicum.shareit.server.request.entity.Request;
import ru.practicum.shareit.server.request.service.RequestService;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.entity.User;
import ru.practicum.shareit.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestServiceTest {
    private final RequestService requestService;
    private final UserService userService;

    private final User user = new User(1L, "user", "email@email.ru");
    private final UserDto userDto = new UserDto(999L, "Someone", "email@email.ru");
    private final UserDto userDto2 = new UserDto(999L, "Someone2", "email2@email.ru");
    private final RequestDto itemRequestDto = new RequestDto(1L, "request", LocalDateTime.of(2024, 2, 2, 2, 2), userDto, List.of());
    private final RequestDto itemGetRequestDto = new RequestDto(1L, "request", LocalDateTime.of(2024, 2, 2, 2, 2), null, List.of());

    @Test
    void createRequest() {
        User thisUser = userService.create(userDto);
        Request thisRequest = requestService.create(itemRequestDto, thisUser.getId());

        assertEquals(thisRequest.getDescription(), itemRequestDto.getDescription());
    }

    @Test
    void throwExceptionIfUserIdIsIncorrect() {
        assertThrows(NotFoundException.class,
                () -> requestService.create(itemRequestDto, 999L));
    }

    @Test
    void getRequestsByOwner() {
        User thisUser = userService.create(userDto);
        Request thisRequest = requestService.create(itemRequestDto, thisUser.getId());
        List<RequestDto> returnedRequest = requestService.getByUserId(thisUser.getId());

        assertFalse(returnedRequest.isEmpty());
    }

    @Test
    void throwExceptionIfUserIdIncorrectGetByUserId() {
        assertThrows(NotFoundException.class,
                () -> requestService.getByUserId(999L));
    }

    @Test
    void throwExceptionIfUserIdIncorrectGetByRequest() {
        assertThrows(NotFoundException.class,
                () -> requestService.getByRequestId(1L, 999L));
    }

    @Test
    void getRequests() {
        User thisUser = userService.create(userDto);
        Request thisRequest = requestService.create(itemRequestDto, thisUser.getId());
        RequestDto returnedRequest = requestService.getByRequestId(thisUser.getId(), thisRequest.getId());

        assertEquals(thisRequest.getDescription(), returnedRequest.getDescription());
        assertEquals(thisRequest.getId(), returnedRequest.getId());
    }

    @Test
    void throwExceptionIfRequestIdIncorrect() {
        userService.create(userDto);
        assertThrows(NotFoundException.class,
                () -> requestService.getByRequestId(1L, 999L));
    }

    @Test
    void throwExceptionIfUserIdIncorrectGetAll() {
        assertThrows(NotFoundException.class,
                () -> requestService.getAll(1L));
    }

    @Test
    void getListAllRequest() {
        User thisUser = userService.create(userDto);
        User anotherUser = userService.create(userDto2);
        Request thisRequest = requestService.create(itemRequestDto, thisUser.getId());
        List<RequestDto> returnedRequest = requestService.getAll(anotherUser.getId());

        assertFalse(returnedRequest.isEmpty());
    }

}
