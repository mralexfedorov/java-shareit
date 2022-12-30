package ru.practicum.shareit.user.storage;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
@AllArgsConstructor
public class UserStorageImpl implements UserStorage {
    private final UserDao userDao;

    @Override
    public User createUser(User user) {
        return userDao.createUser(user);
    }

    @Override
    public User updateUser(User user, int id) {
        return userDao.updateUser(user, id);
    }

    @Override
    public User getUserById(int userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    @Override
    public List<User> findAllUsers() {
        return userDao.findAllUsers();
    }

    @Override
    public void deleteUser(int userId) {
        userDao.deleteUser(userId);
    }
}
