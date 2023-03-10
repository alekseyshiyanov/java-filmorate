package ru.yandex.practicum.filmorate.storage.like;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

@Component("inMemoryLikeStorage")
@Slf4j
public class InMemoryLikeStorage implements LikeStorage {
    @Autowired
    @Qualifier("inMemoryFilmStorage")
    private FilmStorage filmStorage;

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilm(filmId);

        if (film == null) {
            throw new FilmorateNotFoundException("Ошибка добавления лайка. Фильм с ID = " + filmId + " не существует");
        }

        film.getLikesList().add(userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilm(filmId);

        if (film == null) {
            throw new FilmorateNotFoundException("Ошибка удаления лайка. Фильм с ID = " + filmId + " не существует");
        }

        if (!film.getLikesList().contains(userId)) {
            throw new FilmorateNotFoundException("Ошибка удаления лайка. Фильм с ID = " + filmId + " не содержит лайка от пользователя с id = " + userId);
        }

        film.getLikesList().remove(userId);
    }
}
