package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmorateBadRequestException;
import ru.yandex.practicum.filmorate.exceptions.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.genre.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
public class GenreService {

    @Autowired
    @Qualifier("genreDbStorage")
    private GenreStorage genreStorage;

    public List<Genre> getGenreList() {
        return genreStorage.getGenreList();
    }

    public Genre getGenre(Long genreId) {
        checkGenreId(genreId);

        Genre genre = genreStorage.getGenre(genreId);
        if (genre == null) {
            throw new FilmorateNotFoundException("Объект с id = " + genreId + " не найден");
        }
        return genre;
    }

    private void checkGenreId(Long genreId) {
        if (genreId < 0) {
            throw new FilmorateBadRequestException("Параметр 'id' не может быть отрицательным");
        }
    }

}
