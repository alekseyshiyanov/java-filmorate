package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmorateBadRequestException;
import ru.yandex.practicum.filmorate.exceptions.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

/**
 * PUT /users/{id}/friends/{friendId} — добавление в друзья. +
 * DELETE /users/{id}/friends/{friendId} — удаление из друзей. +
 * GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями. +
 * GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем. +
 */
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsersList() {
        log.info("Запрос на получение списка пользователей");
        return userService.getUsersList();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") Long userId) {
        log.info("Запрос на получение данных пользователя с ID={}", userId);
        return userService.getUser(userId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsList(@PathVariable("id") Long userId) {
        log.info("Запрос на получение списка друзей пользователя с ID={}", userId);
        return userService.getFriendsList(userId);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Запрос на создание новой записи");
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Запрос на обновление текущей записи");
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Long userId, @PathVariable("friendId") Long friendId) {
        log.info("Запрос на добавление в друзья");
        userService.addFriends(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Long userId, @PathVariable("friendId") Long friendId) {
        log.info("Запрос на удаление из друзей");
        userService.deleteFriends(userId, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriendsList(@PathVariable("id") Long userId, @PathVariable("otherId") Long otherId) {
        log.info("Запрос на получение списка общих друзей");
        return userService.getCommonFriendsList(userId, otherId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ArrayList<String> errorMap = new ArrayList<>();

        for (FieldError err : e.getBindingResult().getFieldErrors()) {
            errorMap.add(err.getDefaultMessage());
        }

        return errorMap;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleUpdateUserBadRequestException(FilmorateBadRequestException e) {
        return "[\"" + e.getMessage() + "\"]";
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUpdateUserNotFoundException(FilmorateNotFoundException e) {
        return "[\"" + e.getMessage() + "\"]";
    }
}
