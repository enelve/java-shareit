package ru.practicum.shareit.server.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.server.exception.DuplicateEmailException;
import ru.practicum.shareit.server.exception.InvalidEmailException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.entity.User;
import ru.practicum.shareit.server.user.mapper.UserMapper;
import ru.practicum.shareit.server.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {
    private final UserService userService;

    @Test
    void throwExceptionIfEmailIsNull() {
        assertThrows(InvalidEmailException.class,
                () -> userService.create(new UserDto(999L, "name", null)));
    }

    @Test
    void throwExceptionIfEmailIsDuplicate() {
        userService.create(new UserDto(999L, "name", "email@mail.ru"));
        assertThrows(DuplicateEmailException.class,
                () -> userService.create(new UserDto(null, "another name", "email@mail.ru")));
    }


    @Test
    void throwExceptionIfIdIsInvalid() {
        assertThrows(NotFoundException.class,
                () -> userService.getById(1L));
    }

    @Test
    void getById() {
        UserDto userDto = new UserDto(null, "name", "email@email");
        User createdUser = userService.create(userDto);

        assertEquals(createdUser.getEmail(), userService.getById(createdUser.getId()).getEmail());
        assertEquals(createdUser.getName(), userService.getById(createdUser.getId()).getName());
    }

    @Test
    void createUser() {
        UserDto userDto = new UserDto(999L, "name", "email@email");
        User newUser = UserMapper.toUser(userDto);
        User createdUser = userService.create(userDto);

        assertEquals(createdUser.getEmail(), newUser.getEmail());
        assertEquals(createdUser.getName(), newUser.getName());
    }

    @Test
    void updateUser() {
        UserDto userDto = new UserDto(999L, "name", "email@email");
        User thisUser = userService.create(userDto);
        thisUser.setName("NewName");
        User updatedUser = userService.update(userDto, thisUser.getId());

        assertEquals(updatedUser.getEmail(), thisUser.getEmail());
        assertEquals(updatedUser.getName(), thisUser.getName());
    }

    @Test
    void throwExceptionWhenEmailIsEquals() {
        UserDto userDto = new UserDto(999L, "name", "email@email");
        UserDto userDtoAnother = new UserDto(999L, "name", "emailNew@email");
        userService.create(userDtoAnother);
        User user = userService.create(userDto);
        userDto.setEmail("emailNew@email");

        assertThrows(DuplicateEmailException.class,
                () -> userService.update(userDto, user.getId()));
    }

    @Test
    void deleteById() {
        UserDto userDto = new UserDto(null, "name", "email@email");
        User thisUser = userService.create(userDto);
        userService.deleteUser(thisUser.getId());

        assertTrue(userService.getAllUsers().isEmpty());
    }

    @Test
    void throwExceptionWhenDeleteWrongUser() {
        UserDto userDto = new UserDto(999L, "name", "email@email");
        User user = userService.create(userDto);

        assertThrows(NotFoundException.class,
                () -> userService.deleteUser(user.getId() + 1));
    }

    @Test
    void getListOfUsers() {
        User first = userService.create(new UserDto(1L, "user1", "user1@mail.ru"));
        User second = userService.create(new UserDto(2L, "user2", "user2@mail.ru"));

        assertEquals(2, userService.getAllUsers().size());
        assertTrue(userService.getAllUsers().contains(first));
        assertTrue(userService.getAllUsers().contains(second));
    }
}
