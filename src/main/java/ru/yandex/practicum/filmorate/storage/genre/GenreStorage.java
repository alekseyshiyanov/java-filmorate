package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.genre.Genre;

import java.util.List;

public interface GenreStorage {

    List<Genre> getGenreList();

    Genre getGenre(Long genreId);

    List<Genre> getGenreListByFilmId(Long filmId);
}
