package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {
    User createUser(User user);

    User updateUser(User user, int id);

    User getUserById(int userId);

    User getUserByEmail(String email);

    List<User> findAllUsers();

    void deleteUser(int userId);
}
