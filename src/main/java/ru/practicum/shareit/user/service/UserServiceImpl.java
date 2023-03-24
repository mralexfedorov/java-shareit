package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Transactional
    @Override
    public UserDto createUser(UserDto user) {
        String email = user.getEmail();
        if (email == null) {
            throw new ValidationException("Email не может быть пустым.");
        }
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        if (!matcher.find()) {
            throw new ValidationException("Невалидный Email");
        }

        UserDto createdUser = UserMapper.toUserDto(userRepository.save(UserMapper.toUser(user)));

        log.debug("Пользователь {} создан.", createdUser.getName());
        return createdUser;
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto user, int id) {
        User userFromStorage = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException(
                String.format("Пользователь с таким id %s не существует", id)));

        String email = user.getEmail();
        if (email != null) {
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
            if (!matcher.find()) {
                throw new ValidationException("Невалидный Email");
            }
        }

        user.setId(id);
        UserDto updatedUser = UserMapper.toUserDto(userRepository.save(UserMapper.toExistsUser(user,
                userFromStorage)));
        log.debug("Данные о пользователе {} обновлены.", updatedUser.getName());
        return updatedUser;
    }

    @Override
    public UserDto getUser(int userId) {
        User userFromStorage = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException(
                String.format("Пользователь с таким id %s не существует", userId)));

        return UserMapper.toUserDto(userFromStorage);
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
        log.debug("Пользователь с id {} удален.", userId);
    }
}
