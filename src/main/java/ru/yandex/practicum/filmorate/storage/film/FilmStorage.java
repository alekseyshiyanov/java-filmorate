package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilmsList();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(Long filmId);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    List<Film> likedFilmsList(Long count);
}
