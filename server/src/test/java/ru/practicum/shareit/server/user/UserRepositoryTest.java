package ru.practicum.shareit.server.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.exception.DuplicateEmailException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.entity.User;
import ru.practicum.shareit.server.user.mapper.UserMapper;
import ru.practicum.shareit.server.user.repository.UserRepository;
import ru.practicum.shareit.server.user.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {
    private final UserDto user = new UserDto(1L, "name", "email@email.ru");
    @Mock
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    void retrieveUserById() {
        User newUser = UserMapper.toUser(user);
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(newUser));
        User thisUser = userService.getById(1L);
        verify(userRepository, Mockito.times(1)).findById(1L);

        assertEquals(user.getName(), thisUser.getName());
        assertEquals(user.getEmail(), thisUser.getEmail());
    }

    @Test
    void throwExceptionIfUserIdIsInvalid() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.getById(999L));
    }

    @Test
    void throwExceptionIfEmailExists() {
        when(userRepository.save(any()))
                .thenThrow(new DuplicateEmailException(""));
        assertThrows(DuplicateEmailException.class, () -> userService.create(user));
    }
}
