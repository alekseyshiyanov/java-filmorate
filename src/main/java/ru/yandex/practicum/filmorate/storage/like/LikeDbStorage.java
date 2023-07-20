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

    private final String ADD_LIKE_QUERY = "INSERT INTO FilmLikes (User_ID, Film_ID) "
            + "VALUES (?, ?) ON DUPLICATE KEY UPDATE User_ID = User_ID, Film_ID = Film_ID;";
    private final String DELETE_LIKE_QUERY = "DELETE FROM FilmLikes WHERE (User_ID = ?) AND (Film_ID = ?);";
    private final String INC_LIKE_COUNTER_QUERY = "UPDATE FILM SET LikesCount=LikesCount+1 WHERE Film_ID = ?;";
    private final String DEC_LIKE_COUNTER_QUERY = "UPDATE FILM SET LikesCount=LikesCount-1 WHERE (Film_ID = ?) AND (LikesCount > 0);";

    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(Long filmId, Long userId)
    {
        try {
            if (jdbcTemplate.update(ADD_LIKE_QUERY, userId, filmId) > 0)
            {
                jdbcTemplate.update(INC_LIKE_COUNTER_QUERY, filmId);
            }
        } catch (DataAccessException e) {
            log.info("Ошибка добавления лайка от userID = {} для filmID = {}. Причина: {}", userId, filmId, e.getCause().getMessage());
            throw new FilmorateSqlException(String.format("Ошибка добавления лайка от userID = %d для filmID = %d", userId, filmId));
        }
    }

    @Override
    public void deleteLike(Long filmId, Long userId)
    {
        try {
            if (jdbcTemplate.update(DELETE_LIKE_QUERY, userId, filmId) > 0)
            {
                jdbcTemplate.update(DEC_LIKE_COUNTER_QUERY, filmId);
            }
        } catch (DataAccessException e) {
            log.info("Ошибка удаления лайка от userID = {} для filmID = {}. Причина: {}", userId, filmId, e.getCause().getMessage());
            throw new FilmorateSqlException(String.format("Ошибка удаления лайка от userID = %d для filmID = %d", userId, filmId));
        }
    }
}
