package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    UserDto createUser(UserDto user);

    UserDto updateUser(UserDto user, int id);

    UserDto getUserById(int userId);

    UserDto getUserByEmail(String email);

    List<UserDto> findAllUsers();

    void deleteUser(int userId);
}
