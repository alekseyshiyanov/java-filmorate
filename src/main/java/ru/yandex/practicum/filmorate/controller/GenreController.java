package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.genre.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@Slf4j
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<Genre> getGenreList() {
        log.info("Запрос на получение списка жанров");
        return genreService.getGenreList();
    }
    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable("id") Long genreId) {
        log.info("Запрос на получение данных жанров с ID={}", genreId);
        return genreService.getGenre(genreId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUpdateUserNotFoundException(FilmorateNotFoundException e) {
        return "[\"" + e.getMessage() + "\"]";
    }
}
