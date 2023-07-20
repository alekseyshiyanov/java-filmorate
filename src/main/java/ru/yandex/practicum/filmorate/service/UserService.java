package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService {

    @Autowired
    @Qualifier("userDbStorage")
    private UserStorage userStorage;

    @Autowired
    @Qualifier("friendsDbStorage")
    private FriendsStorage friendsStorage;

    public UserService() {}

    public List<User> getUsersList() {
        return userStorage.getUsersList();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUser(Long userId) {
        checkUserId(userId);

        User user = userStorage.getUser(userId);
        if (user == null) {
            throw new FilmorateNotFoundException("Пользователь с id = " + userId + " не найден");
        }
        return user;
    }

    public void addFriends(Long userId, Long friendId) {
        checkUserId(friendId);
        checkUserId(userId);

        friendsStorage.addFriends(userId, friendId);
    }

    public void deleteFriends(Long userId, Long friendId) {
        checkUserId(friendId);
        checkUserId(userId);

        friendsStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriendsList(Long userId) {
        checkUserId(userId);

        return userStorage.getFriendsList(userId);
    }

    public List<User> getCommonFriendsList(Long userId, Long otherId) {
        checkUserId(userId);
        checkUserId(otherId);

        return userStorage.getCommonFriendsList(userId, otherId);
    }

    private void checkUserId(Long userId) {
        if (userId < 0) {
            throw new FilmorateNotFoundException("Пользователь с userId = " + userId + " не существует");
        }
    }
}
