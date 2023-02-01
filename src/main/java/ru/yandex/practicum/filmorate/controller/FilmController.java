package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmorateBadRequestException;
import ru.yandex.practicum.filmorate.exceptions.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private Long filmUID = 0L;
    private final HashMap<Long, Film> films = new HashMap<>();

    private Long getFilmUID() {
        return ++filmUID;
    }

    @GetMapping
    public List<Film> getFilmsList() {
        log.info("Текущее количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }


    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        addFilm(film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        Long uid = film.getId();

        if (uid == null) {
            log.info("Объект: {} не может быть сохранен. Причина 'ID не должен быть null'", film);
            throw new FilmorateBadRequestException("Ошибка обновления объекта. ID не должен быть null");
        }

        if (uid < 0L) {
            log.info("Объект: {} не может быть сохранен. Причина 'ID должен быть положительным числом'", film);
            throw new FilmorateBadRequestException("Ошибка обновления объекта. ID должен быть положительным числом");
        }

        Film oldFilm = films.get(film.getId());

        if (oldFilm == null) {
            log.info("Объект: {} не может быть сохранен. Причина 'Объект с ID = {} не существует'", film, uid);
            throw new FilmorateNotFoundException("Ошибка обновления объекта. Объект с ID = " + uid + " не существует");
        }

        log.info("Обновляем объект с ID: {}", oldFilm.getId());
        log.info("Объект до обновления: {}", oldFilm);

        filmAddOrUpdate(film);

        log.info("Объект после обновления: {}", films.get(film.getId()));

        return film;
    }

    private void addFilm(Film film) {
        film.setId(getFilmUID());
        filmAddOrUpdate(film);
        log.info("Сохранен объект: {}", film);
    }

    private void checkReleaseDate(Film film) {
        LocalDate releaseDate = film.getReleaseDate();

        if (releaseDate == null) {
            log.info("Объект: {} не может быть сохранен. Причина 'Дата релиза не может быть null'", film);
            throw new FilmorateBadRequestException("Дата релиза не может быть null");
        }

        if(releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Объект: {} не может быть сохранен. Причина 'Дата релиза не может быть ранее 1895-12-28'", film);
            throw new FilmorateBadRequestException("Дата релиза не может быть ранее 1895-12-28");
        }
    }

    private void filmAddOrUpdate(Film film) {
        checkReleaseDate(film);
        films.put(film.getId(), film);
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ArrayList<String> errorMap = new ArrayList<>();

        for (FieldError err : e.getBindingResult().getFieldErrors()) {
            errorMap.add(err.getDefaultMessage());
        }

        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FilmorateNotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(FilmorateNotFoundException e) {
        return new ResponseEntity<>("[\n\t\"" + e.getMessage() + "\"\n]", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FilmorateBadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(FilmorateBadRequestException e) {
        return new ResponseEntity<>("[\n\t\"" + e.getMessage() + "\"\n]", HttpStatus.BAD_REQUEST);
    }
}
