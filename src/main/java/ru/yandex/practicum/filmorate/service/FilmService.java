package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmorateBadRequestException;
import ru.yandex.practicum.filmorate.exceptions.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.time.LocalDate;
import java.util.List;

@Service
public class FilmService {

    @Autowired
    @Qualifier("filmDbStorage")
    private FilmStorage filmStorage;

    @Autowired
    @Qualifier("likeDbStorage")
    private LikeStorage likeStorage;

    public List<Film> getFilmsList() {
        return filmStorage.getFilmsList();
    }

    public Film createFilm(Film film) {
        checkReleaseDate(film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        checkReleaseDate(film);
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

    public void addLike(Long filmId, Long userId) {
        checkFilmId(filmId);
        checkUserId(userId);

        likeStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        checkFilmId(filmId);
        checkUserId(userId);

        likeStorage.deleteLike(filmId, userId);
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

    private void checkReleaseDate(Film film) {
        LocalDate releaseDate = film.getReleaseDate();

        if (releaseDate == null) {
            throw new FilmorateBadRequestException("Дата релиза не может быть null");
        }

        if(releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            throw new FilmorateBadRequestException("Дата релиза не может быть ранее 1895-12-28");
        }
    }

}
