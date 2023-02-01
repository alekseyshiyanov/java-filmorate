package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmorateBadRequestException;
import ru.yandex.practicum.filmorate.exceptions.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private Long filmUID = 0L;
    private final HashMap<Long, Film> films = new HashMap<>();

    private Long getFilmUID() {
        return ++filmUID;
    }

    @Override
    public List<Film> getFilmsList() {
        log.info("Текущее количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }


    @Override
    public Film createFilm(Film film) {
        addFilm(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        Long uid = film.getId();

        if (uid == null) {
            log.info("Объект: {} не может быть сохранен. Причина 'ID не должен быть null'", film);
            throw new FilmorateBadRequestException("Ошибка обновления объекта. ID не должен быть null");
        }

        if (uid < 0L) {
            log.info("Объект: {} не может быть сохранен. Причина 'ID должен быть положительным числом'", film);
            throw new FilmorateBadRequestException("Ошибка обновления объекта. ID должен быть положительным числом");
        }

        Film oldFilm = films.get(uid);

        if (oldFilm == null) {
            log.info("Объект: {} не может быть сохранен. Причина 'Объект с ID = {} не существует'", film, uid);
            throw new FilmorateNotFoundException("Ошибка обновления объекта. Объект с ID = " + uid + " не существует");
        }

        log.info("Обновляем объект с ID: {}", uid);
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
}
