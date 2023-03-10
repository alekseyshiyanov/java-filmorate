package ru.yandex.practicum.filmorate.storage.like;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.FilmorateSqlException;

@Slf4j
@Component("likeDbStorage")
@Repository
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    private final String ADD_LIKE_QUERY = "INSERT INTO FilmLikes (UserID, FilmID) "
            + "VALUES (?, ?) ON DUPLICATE KEY UPDATE UserID = UserID, FilmID = FilmID;";
    private final String DELETE_LIKE_QUERY = "DELETE FROM FilmLikes WHERE (UserID = ?) AND (FilmID = ?);";

    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int addLike(Long filmId, Long userId)
    {
        try {
            return jdbcTemplate.update(ADD_LIKE_QUERY, userId, filmId);
        } catch (DataAccessException e) {
            log.info("Ошибка добавления лайка от userID = {} для filmID = {}. Причина: {}", userId, filmId, e.getCause().getMessage());
            throw new FilmorateSqlException(String.format("Ошибка добавления лайка от userID = %d для filmID = %d", userId, filmId));
        }
    }

    @Override
    public int deleteLike(Long filmId, Long userId)
    {
        try {
            return jdbcTemplate.update(DELETE_LIKE_QUERY, userId, filmId);
        } catch (DataAccessException e) {
            log.info("Ошибка удаления лайка от userID = {} для filmID = {}. Причина: {}", userId, filmId, e.getCause().getMessage());
            throw new FilmorateSqlException(String.format("Ошибка удаления лайка от userID = %d для filmID = %d", userId, filmId));
        }
    }
}
