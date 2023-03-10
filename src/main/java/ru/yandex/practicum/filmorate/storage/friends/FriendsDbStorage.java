package ru.yandex.practicum.filmorate.storage.friends;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.FilmorateSqlException;

@Slf4j
@Component("friendsDbStorage")
@Repository
public class FriendsDbStorage implements FriendsStorage {

    private final JdbcTemplate jdbcTemplate;

    public FriendsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriends(Long userId, Long friendId)
    {
        String sqlQueryUpdate = "UPDATE Friends SET Status = ? WHERE (User_From = ?) AND (User_To = ?);";
        String sqlQueryInsert = "INSERT INTO Friends (User_From, User_To, Status) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE User_From = User_From, User_To = User_To;";

        try {
            int result = jdbcTemplate.update(sqlQueryUpdate, 1, friendId, userId);
            jdbcTemplate.update(sqlQueryInsert, userId, friendId, result);
        } catch (DataAccessException e) {
            log.info("Ошибка добавления друга friendId = {} для userID = {}. Причина: {}", friendId, userId, e.getCause().getMessage());
            throw new FilmorateSqlException("Ошибка добавления друга");
        }
    }

    @Override
    public void deleteFriend(Long userId, Long friendId)
    {
        String sqlQueryUpdate = "UPDATE Friends SET Status = ? WHERE (User_From = ?) AND (User_To = ?);";
        String sqlQueryDelete = "DELETE FROM FRIENDS WHERE (User_From = ?) AND (User_To = ?);";

        try {
            jdbcTemplate.update(sqlQueryUpdate, 0, friendId, userId);
            jdbcTemplate.update(sqlQueryDelete, userId, friendId);

        } catch (DataAccessException e) {
            log.info("Ошибка при чтении данных пользователя. Причина: {}", e.getCause().getMessage());
            throw new FilmorateSqlException("Ошибка при чтении данных пользователя");
        }
    }
}
