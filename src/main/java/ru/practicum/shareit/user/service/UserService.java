package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User create(UserDto userDto) {
        if (userDto.getEmail() == null) throw new InvalidEmailException("Empty email");
        checkEmail(userDto.getEmail());
        return userRepository.create(userDto);
    }

    public User update(UserDto user, Long id) {
        if (user.getEmail() != null && !getById(id).getEmail().equals(user.getEmail())) {
            checkEmail(user.getEmail());
        }
        UserDto userDto = UserMapper.toUserDto(user, getById(id));
        User updatedUser = UserMapper.dtoToUser(userDto);
        updatedUser.setId(id);
        return userRepository.update(updatedUser, id);
    }

    public User getById(Long id) {
        return userRepository.getById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User not found %s", id)));
    }

    public Collection<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public User deleteUser(Long id) {
        if (userRepository.getById(id).isPresent()) {
            return userRepository.deleteUser(id);
        } else {
            throw new NotFoundException(String.format("User not found %s", id));
        }
    }

    private boolean checkEmail(String email) {
        for (User u : userRepository.getAllUsers()) {
            if (u.getEmail().equals(email))
                throw new DuplicateEmailException(String.format("DuplicateEmailException: %s", email));
        }
        return true;
    }
}
