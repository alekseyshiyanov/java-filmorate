package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.mpa.MPA;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class MpaController {

    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public List<MPA> getMpaList() {
        log.info("Запрос на получение списка рейтингов MPA");
        return mpaService.getMpaList();
    }
    @GetMapping("/{id}")
    public MPA getMpa(@PathVariable("id") Long mpaId) {
        log.info("Запрос на получение данных рейтинга MPA с ID={}", mpaId);
        return mpaService.getMpa(mpaId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUpdateUserNotFoundException(FilmorateNotFoundException e) {
        return "[\"" + e.getMessage() + "\"]";
    }

}
