package ru.yandex.practicum.filmorate.storage.friends;

public interface FriendsStorage {
    void addFriends(Long userId, Long friendId);
    void deleteFriend(Long userId, Long friendId);
}
