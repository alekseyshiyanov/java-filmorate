package ru.yandex.practicum.filmorate.exceptions;

public class FilmorateSqlException extends RuntimeException {
    public FilmorateSqlException(String message) {
        super(message);
    }
}
