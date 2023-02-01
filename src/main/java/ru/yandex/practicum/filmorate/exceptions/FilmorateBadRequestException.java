package ru.yandex.practicum.filmorate.exceptions;

public class FilmorateBadRequestException extends RuntimeException {
    public FilmorateBadRequestException(String message) {
        super(message);
    }
}
