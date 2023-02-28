package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getUsersList();

    User createUser(User user);

    User updateUser(User user);

    User getUser(Long userId);

    void addFriends(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    List<User> getFriendsList(Long userId);

    List<User> getCommonFriendsList(Long userId, Long otherId);
}
