package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserEmailDuplicatedException;
import ru.practicum.shareit.exceptions.UserEmailEmptyException;
import ru.practicum.shareit.exceptions.UserEmailNotValidException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    public UserDto createUser(UserDto user) {
        String email = user.getEmail();
        if (email == null) {
            throw new UserEmailEmptyException("Email не может быть пустым.");
        }
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        if (!matcher.find()) {
            throw new UserEmailNotValidException("Невалидный Email");
        }
        if (userStorage.getUserByEmail(user.getEmail()) != null) {
            throw new UserEmailDuplicatedException("Такой email уже существует.");
        }

        UserDto createdUser = UserMapper.toUserDto(userStorage.createUser(UserMapper.toUser(user)));

        log.debug("Пользователь {} создан.", createdUser.getName());
        return createdUser;
    }

    @Override
    public UserDto updateUser(UserDto user, int id) {
        User userFromStorage = userStorage.getUserById(id);
        if (userFromStorage == null) {
            throw new UserNotFoundException(
                    String.format("Пользователь с таким id %s не существует", id));
        }
        String email = user.getEmail();
        if (email != null) {
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
            if (!matcher.find()) {
                throw new UserEmailNotValidException("Невалидный Email");
            }
            if (userStorage.getUserByEmail(email) != null) {
                throw new UserEmailDuplicatedException("Такой email уже существует.");
            }
        }

        user.setId(id);
        UserDto updatedUser = UserMapper.toUserDto(userStorage.updateUser(UserMapper.toExistsUser(user,
                userFromStorage), id));
        log.debug("Данные о пользователе {} обновлены.", updatedUser.getName());
        return updatedUser;
    }

    @Override
    public UserDto getUser(int userId) {
        User userFromStorage = userStorage.getUserById(userId);
        if (userFromStorage == null) {
            throw new UserNotFoundException(
                    String.format("Пользователь с таким id %s не существует", userId));
        }
        return UserMapper.toUserDto(userStorage.getUserById(userId));
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userStorage.findAllUsers().stream().map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(int userId) {
        userStorage.deleteUser(userId);
        log.debug("Пользователь с id {} удален.", userId);
    }
}
