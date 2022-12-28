package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class UserDaoImpl implements UserDao{
    private HashMap<Integer, User> users;
    private int id = 0;

    public UserDaoImpl(HashMap<Integer, User> users) {
        this.users = users;
    }

    @Override
    public User createUser(User user) {
        if (user.getId() == 0) {
            id++;
            user.setId(id);
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user, int id) {
        users.put(id, user);
        return user;
    }

    @Override
    public User getUserById(int userId) {
        return users.get(userId);
    }

    @Override
    public User getUserByEmail(String email) {
        HashMap<String, User> usersWithEmails = new HashMap<>();
        users.forEach((key, value) -> {
            usersWithEmails.put(value.getEmail(), value);
        });

        return usersWithEmails.get(email);
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList(users.values());
    }

    @Override
    public void deleteUser(int userId) {
        users.remove(userId);
    }
}
