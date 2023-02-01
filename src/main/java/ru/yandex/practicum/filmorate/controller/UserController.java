package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmorateBadRequestException;
import ru.yandex.practicum.filmorate.exceptions.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

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
        log.info("Получен запрос на  получение списка пользователей");
        return userService.getUsersList();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Создание новой записи");
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Запрос на обновление текущей записи");
        return userService.updateUser(user);
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
