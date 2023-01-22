package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmorateBadRequestException;
import ru.yandex.practicum.filmorate.exceptions.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private Long userUID = 0L;
    private final HashMap<Long, User> users = new HashMap<>();

    @GetMapping
    public List<User> getUsersList() {
        log.info("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        addUser(user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        Long uid = user.getId();

        if (uid == null) {
            log.info("Объект: {} не может быть сохранен. Причина 'ID не должен быть null'", user);
            throw new FilmorateBadRequestException("Ошибка обновления объекта. ID не должен быть null");
        }

        if (uid < 0L) {
            log.info("Объект: {} не может быть сохранен. Причина 'ID должен быть положительным числом'", user);
            throw new FilmorateBadRequestException("Ошибка обновления объекта. ID должен быть положительным числом");
        }

        User oldUser = users.get(user.getId());

        if (oldUser == null) {
            log.info("Объект: {} не может быть сохранен. Причина 'Объект с ID = {} не существует'", user, uid);
            throw new FilmorateNotFoundException("Ошибка обновления объекта. Объект с ID = " + uid + " не существует");
        }

        log.info("Обновляем объект с ID: {}", oldUser.getId());
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ArrayList<String> errorMap = new ArrayList<>();

        for (FieldError err : e.getBindingResult().getFieldErrors()) {
            errorMap.add(err.getDefaultMessage());
        }

        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FilmorateBadRequestException.class)
    public ResponseEntity<String> handleUpdateUserBadRequestException(FilmorateBadRequestException e) {
        return new ResponseEntity<>("[\n\t\"" + e.getMessage() + "\"\n]", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FilmorateNotFoundException.class)
    public ResponseEntity<String> handleUpdateUserNotFoundException(FilmorateNotFoundException e) {
        return new ResponseEntity<>("[\n\t\"" + e.getMessage() + "\"\n]", HttpStatus.NOT_FOUND);
    }
}
