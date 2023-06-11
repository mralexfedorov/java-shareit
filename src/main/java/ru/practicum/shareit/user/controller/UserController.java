package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping()
    public UserDto createUser(@RequestBody @Valid UserDto user) {
        log.info("Создание пользователя:" + user);
        return userService.createUser(user);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH)
    public UserDto updateUser(@RequestBody UserDto user, @PathVariable("userId") int id) {
        log.info("Обновление пользователя:" + user + ", id=" + id);
        return userService.updateUser(user, id);
    }

    @GetMapping()
    public List<UserDto> findAllUsers() {
        log.info("Получение всех пользователей");
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable("id") int userId) {
        log.info("Получение пользователя с id=" + userId);
        return userService.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) {
        log.info("Удаление пользователя с id=" + userId);
        userService.deleteUser(userId);
    }
}
