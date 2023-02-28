package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmorateBadRequestException;
import ru.yandex.practicum.filmorate.exceptions.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private Long userUID = 0L;
    private final HashMap<Long, User> users = new HashMap<>();

    @Override
    public List<User> getUsersList() {
        log.info("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        addUser(user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        Long uid = user.getId();

        if (uid == null) {
            log.info("Объект: {} не может быть сохранен. Причина 'ID не должен быть null'", user);
            throw new FilmorateBadRequestException("Ошибка обновления объекта. ID не должен быть null");
        }

        if (uid < 0L) {
            log.info("Объект: {} не может быть сохранен. Причина 'ID должен быть положительным числом'", user);
            throw new FilmorateBadRequestException("Ошибка обновления объекта. ID должен быть положительным числом");
        }

        User oldUser = users.get(uid);

        if (oldUser == null) {
            log.info("Объект: {} не может быть сохранен. Причина 'Объект с ID = {} не существует'", user, uid);
            throw new FilmorateNotFoundException("Ошибка обновления объекта. Объект с ID = " + uid + " не существует");
        }

        log.info("Обновляем объект с ID: {}", uid);
        log.info("Объект до обновления: {}", oldUser);

        checkUserName(user);
        checkFriendsList(user);

        users.put(user.getId(), user);

        log.info("Объект после обновления: {}", users.get(user.getId()));

        return user;
    }

    @Override
    public User getUser(Long userId) {
        return users.get(userId);
    }

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

        if (!user_1.getFriends().contains(friendId)) {
            throw new FilmorateNotFoundException("Пользователь с ID = " + userId + " не связан пользователем с ID = " + friendId);
        }

        if (!user_2.getFriends().contains(userId)) {
            throw new FilmorateNotFoundException("Пользователь с ID = " + friendId + " не связан пользователем с ID = " + userId);
        }

        user_1.getFriends().remove(friendId);
        user_2.getFriends().remove(userId);
    }

    @Override
    public List<User> getFriendsList(Long userId) {
        User user = checkUser(userId);

        return users.values().stream()
                             .filter(c -> user.getFriends().contains(c.getId()))
                             .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriendsList(Long userId, Long otherId) {
        User user_1 = checkUser(userId);
        User user_2 = checkUser(otherId);

        return users.values().stream()
                             .filter(c -> user_1.getFriends().contains(c.getId()))
                             .filter(c -> user_2.getFriends().contains(c.getId()))
                             .collect(Collectors.toList());
    }

    private User checkUser(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new FilmorateNotFoundException("Пользователь с ID = " + userId + " не существует");
        }
        return user;
    }

    private void addUser(User user) {
        checkUserName(user);
        checkFriendsList(user);

        Long uid = getUserUID();

        user.setId(uid);
        users.put(user.getId(), user);

        log.info("Сохранен объект: {}", user);
    }

    private void checkUserName(User user) {
        if ((user.getName() == null) || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void checkFriendsList(User user) {
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
    }

    private Long getUserUID() {
        return ++userUID;
    }
}
