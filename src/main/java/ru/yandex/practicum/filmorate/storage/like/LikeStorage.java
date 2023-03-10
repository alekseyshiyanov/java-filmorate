package ru.yandex.practicum.filmorate.storage.like;

public interface LikeStorage {
    int addLike(Long filmId, Long userId);
    int deleteLike(Long filmId, Long userId);
}
