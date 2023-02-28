package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmorateBadRequestException;
import ru.yandex.practicum.filmorate.exceptions.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> getFilmsList() {
        return filmStorage.getFilmsList();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(Long filmId) {
        checkFilmId(filmId);

        Film film = filmStorage.getFilm(filmId);
        if (film == null) {
            throw new FilmorateNotFoundException("Объект с id = " + filmId + " не найден");
        }
        return film;
    }

    public Film addLike(Long filmId, Long userId) {
        checkFilmId(filmId);
        checkUserId(userId);

        return filmStorage.addLike(filmId, userId);
    }

    public Film deleteLike(Long filmId, Long userId) {
        checkFilmId(filmId);
        checkUserId(userId);

        return filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> likedFilmsList (Long count) {
        Long filmsCount = 10L;

        if (count != null) {
            filmsCount = count;
        }

        return filmStorage.likedFilmsList(filmsCount);
    }

    private void checkFilmId(Long filmId) {
        if (filmId < 0) {
            throw new FilmorateBadRequestException("Параметр 'id' не может быть отрицательным");
        }
    }

    private void checkUserId(Long userId) {
        if (userId < 0) {
            throw new FilmorateNotFoundException("Пользователь с userId = " + userId + " не существует");
        }
    }
}
