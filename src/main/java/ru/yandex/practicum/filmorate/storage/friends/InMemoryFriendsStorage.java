package ru.yandex.practicum.filmorate.storage.friends;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Component("inMemoryFriendsStorage")
@Slf4j
public class InMemoryFriendsStorage implements FriendsStorage {

    @Autowired
    @Qualifier("inMemoryUserStorage")
    private UserStorage userStorage;

    @Override
    public void addFriends(Long userId, Long friendId) {
        User user_1 = checkUser(userId);
        User user_2 = checkUser(friendId);

        user_1.getFriends().add(friendId);
        user_2.getFriends().add(userId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        User user_1 = checkUser(userId);
        User user_2 = checkUser(friendId);

        user_1.getFriends().remove(friendId);
        user_2.getFriends().remove(userId);
    }

    private User checkUser(Long userId) {
        User user = userStorage.getUser(userId);
        if (user == null) {
            throw new FilmorateNotFoundException("Пользователь с ID = " + userId + " не существует");
        }
        return user;
    }

}
