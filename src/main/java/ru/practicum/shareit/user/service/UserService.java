package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto user);

    UserDto updateUser(UserDto user,  int id);

    UserDto getUser(int userId);

    List<UserDto> findAllUsers();

    void deleteUser(int userId);
}
