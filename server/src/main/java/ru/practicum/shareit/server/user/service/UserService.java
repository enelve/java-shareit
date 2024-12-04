package ru.practicum.shareit.server.user.service;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.exception.DuplicateEmailException;
import ru.practicum.shareit.server.exception.InvalidEmailException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.entity.User;
import ru.practicum.shareit.server.user.mapper.UserMapper;
import ru.practicum.shareit.server.user.repository.UserRepository;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public User create(UserDto userDto) {
        if (userDto.getEmail() == null) throw new InvalidEmailException("Empty email");
        try {
            checkEmail(userDto.getEmail());
            return userRepository.save(UserMapper.toUser(userDto));
        } catch (ConstraintViolationException | NullPointerException s) {
            throw new DuplicateEmailException(String.format("Duplicated email %s", userDto.getId()));
        }
    }

    public User update(UserDto user, Long id) {
        if (user.getEmail() != null && !getById(id).getEmail().equals(user.getEmail())) {
            checkEmail(user.getEmail());
        }
        UserDto userDto = UserMapper.toUserDto(user, getById(id));
        User updatedUser = UserMapper.toUser(userDto);
        updatedUser.setId(id);
        return userRepository.save(updatedUser);
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User not found %s", id)));
    }

    @Transactional(readOnly = true)
    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new NotFoundException(String.format("User not found %s", id));
        }
    }

    private void checkEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException(String.format("DuplicateEmailException: %s", email));
        }
    }
}
