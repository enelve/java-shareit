package ru.practicum.shareit.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserDto toUserDto(UserDto user, User initUser) {
        return new UserDto(user.getId() != null ? user.getId() : initUser.getId(),
                user.getName() != null ? user.getName() : initUser.getName(),
                user.getEmail() != null ? user.getEmail() : initUser.getEmail());
    }

    public static User toUser(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }
}
