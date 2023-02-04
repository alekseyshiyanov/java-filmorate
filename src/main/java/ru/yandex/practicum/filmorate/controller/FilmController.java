package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.yandex.practicum.filmorate.exceptions.FilmorateBadRequestException;
import ru.yandex.practicum.filmorate.exceptions.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 *   GET /films/{id}                     — добавьте возможность получать каждый фильм и данные о пользователях по их уникальному идентификатору.
 *   PUT /films/{id}/like/{userId}       — пользователь ставит лайк фильму. +
 *   DELETE /films/{id}/like/{userId}    — пользователь удаляет лайк. +
 *   GET /films/popular?count={count}    — возвращает список из первых count фильмов по количеству лайков. Если значение параметра count не задано, верните первые 10.
 */

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilmsList() {
        log.info("Запрос на получение списка фильмов");
        return filmService.getFilmsList();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Запрос на создание новой записи");
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Запрос на обновление текущей записи");
        return filmService.updateFilm(film);
    }

    @PutMapping(value = {"/{id}/like/{userId}", "/{id}/like/", "/like/{userId}"})
    public Film addLike(@PathVariable("id") Long filmId, @PathVariable("userId") Long userId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping(value = {"/{id}/like/{userId}", "/{id}/like/", "/like/{userId}"})
    public Film deleteLike(@PathVariable("id") Long filmId, @PathVariable("userId") Long userId) {
        return filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable("id") Long filmId) {
        return filmService.getFilm(filmId);
    }

    @GetMapping("/popular")
    public List<Film> likedFilmsList(@RequestParam(required = false) Long count) {
        return filmService.likedFilmsList(count);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ArrayList<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ArrayList<String> errorMap = new ArrayList<>();

        for (FieldError err : e.getBindingResult().getFieldErrors()) {
            errorMap.add(err.getDefaultMessage());
        }

        return errorMap;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(FilmorateNotFoundException e) {
        return "[\"" + e.getMessage() + "\"]";
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequestException(FilmorateBadRequestException e) {
        return "[\"" + e.getMessage() + "\"]";
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequestException(MethodArgumentTypeMismatchException e) {
        return "[\"Параметр '" + e.getName() + "' должен быть числом\"]";
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequestException(MissingPathVariableException e) {
        return "[\"Пропущен обязательный параметр '" + e.getVariableName() + "'\"]";
    }
}
