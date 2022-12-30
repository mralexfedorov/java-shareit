package ru.practicum.shareit.user.storage;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class UserStorageImpl implements UserStorage {
    private final UserDao userDao;

    @Override
    public UserDto createUser(UserDto user) {
        return UserMapper.toUserDto(userDao.createUser(UserMapper.toUser(user)));
    }

    @Override
    public UserDto updateUser(UserDto user, int id) {
        User userFromStorage = userDao.getUserById(id);

        return UserMapper.toUserDto(userDao.updateUser(UserMapper.toExistsUser(user,
                userFromStorage), id));
    }

    @Override
    public UserDto getUserById(int userId) {
        User user = userDao.getUserById(userId);
        if (user == null) {
            return null;
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userDao.getUserByEmail(email);
        if (user == null) {
            return null;
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userDao.findAllUsers().stream().map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(int userId) {
        userDao.deleteUser(userId);
    }
}
