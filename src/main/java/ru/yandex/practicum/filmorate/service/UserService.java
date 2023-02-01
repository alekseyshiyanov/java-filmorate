package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsersList() {
        return userStorage.getUsersList();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }


    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }
}
