package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmorateBadRequestException;
import ru.yandex.practicum.filmorate.exceptions.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    public Film getFilm(Long filmId) {
        return films.get(filmId);
    }

    @Override
    public Film updateFilm(Film film) {
        checkReleaseDate(film);

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

        if (film.getLikesList() == null) {
            film.setLikesList(new HashSet<>());
        }

        films.put(uid, film);

        log.info("Объект после обновления: {}", films.get(film.getId()));

        return film;
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        Film film = films.get(filmId);

        if (film == null) {
            throw new FilmorateNotFoundException("Ошибка добавления лайка. Фильм с ID = " + filmId + " не существует");
        }

        film.getLikesList().add(userId);
        return film;
    }

    @Override
    public Film deleteLike(Long filmId, Long userId) {
        Film film = films.get(filmId);

        if (film == null) {
            throw new FilmorateNotFoundException("Ошибка удаления лайка. Фильм с ID = " + filmId + " не существует");
        }

        if (!film.getLikesList().contains(userId)) {
            throw new FilmorateNotFoundException("Ошибка удаления лайка. Фильм с ID = " + filmId + " не содержит лайка от пользователя с id = " + userId);
        }

        film.getLikesList().remove(userId);
        return film;
    }

    @Override
    public List<Film> likedFilmsList(Long count) {
        return  films.values().stream()
                .sorted(Comparator.comparingInt(film -> (-1 * film.getLikesList().size())))
                .limit((films.size() < count) ? films.size() : count)
                .collect(Collectors.toList());
    }

    private void addFilm(Film film) {
        if (film.getLikesList() == null) {
            film.setLikesList(new HashSet<>());
        }

        checkReleaseDate(film);
        film.setId(getFilmUID());
        films.put(film.getId(), film);
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
}
