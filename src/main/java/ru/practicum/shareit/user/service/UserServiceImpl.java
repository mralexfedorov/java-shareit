package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto createUser(UserDto user) {
        return userStorage.createUser(user);
    }

    @Override
    public UserDto updateUser(UserDto user, int id) {
        return userStorage.updateUser(user, id);
    }

    @Override
    public UserDto getUser(int userId) {
        return userStorage.getUser(userId);
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userStorage.findAllUsers();
    }

    @Override
    public void deleteUser(int userId) {
        userStorage.deleteUser(userId);
    }
}
