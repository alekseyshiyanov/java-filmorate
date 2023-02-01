package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmorateBadRequestException;
import ru.yandex.practicum.filmorate.exceptions.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

        userAddOrUpdate(user);

        log.info("Объект после обновления: {}", users.get(user.getId()));

        return user;
    }

    private void addUser(User user) {
        Long uid = getUserUID();
        user.setId(uid);
        userAddOrUpdate(user);
        log.info("Сохранен объект: {}", user);
    }

    private void checkUserName(User user) {
        if ((user.getName() == null) || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void userAddOrUpdate(User user) {
        checkUserName(user);
        users.put(user.getId(), user);
    }

    private Long getUserUID() {
        return ++userUID;
    }
}
