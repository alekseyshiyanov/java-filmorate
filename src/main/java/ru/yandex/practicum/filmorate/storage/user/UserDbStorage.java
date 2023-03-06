package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.FilmorateSqlException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component("userDbStorage")
@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getUsersList()
    {
        try {
            return jdbcTemplate.query("SELECT * FROM USER;", this::getUserDataFromQuery);
        } catch (DataAccessException e) {
            log.info("Ошибка при чтении данных пользователя. Причина: {}", e.getCause().getMessage());
            throw new FilmorateSqlException("Ошибка при чтении данных пользователя");
        }
    }

    @Override
    public User createUser(User user)
    {
        checkUserName(user);

        String sqlInsertQuery = "INSERT INTO USER (Login, Name, Email, Birthday) VALUES (?, ?, ?, ?);";

        try {
            jdbcTemplate.update(sqlInsertQuery,
                    user.getLogin(),
                    user.getName(),
                    user.getEmail(),
                    user.getBirthday().format(DateTimeFormatter.ISO_DATE));

            return jdbcTemplate.queryForObject("SELECT * FROM USER ORDER BY ID DESC LIMIT 1;", this::getUserDataFromQuery);
        } catch (DataAccessException e) {
            log.info("Ошибка при добавлении нового пользователя. Причина: {}", e.getCause().getMessage());
            throw new FilmorateSqlException("Ошибка при добавлении нового пользователя");
        }
    }

    @Override
    public User updateUser(User user)
    {
        String sqlUpdateQuery = "UPDATE USER "
                              + "SET Login = ?, Name = ?, Email = ?, Birthday = ? "
                              + "WHERE ID = ?;";

        try {
            Long userId = user.getId();

            int result = jdbcTemplate.update(sqlUpdateQuery,
                    user.getLogin(),
                    user.getName(),
                    user.getEmail(),
                    user.getBirthday().format(DateTimeFormatter.ISO_DATE),
                    userId);

            if (result == 0) {
                throw new FilmorateNotFoundException("Ошибка при обновлении данных пользователя. Пользователь с ID = " + userId + " не существует");
            }

            return jdbcTemplate.queryForObject("SELECT * FROM USER WHERE ID = ?;", this::getUserDataFromQuery, userId);
        } catch (DataAccessException e) {
            log.info("Ошибка при обновлении данных пользователя. Причина: {}", e.getCause().getMessage());
            throw new FilmorateSqlException("Ошибка при обновлении данных пользователя");
        }
    }

    @Override
    public User getUser(Long userId)
    {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM USER WHERE ID = ?;", this::getUserDataFromQuery, userId);
        } catch (EmptyResultDataAccessException e) {
            log.info("Ошибка при чтении данных пользователя. Причина: {}", e.getMessage());
            throw new FilmorateNotFoundException("Пользователь с ID = " + userId + " не найден");
        } catch (DataAccessException e) {
            log.info("Ошибка при чтении данных пользователя. Причина: {}", e.getCause().getMessage());
            throw new FilmorateSqlException("Ошибка при чтении данных пользователя");
        }
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

    @Override
    public List<User> getFriendsList(Long userId)
    {
        String sqlQuery =   "SELECT * " +
                            "FROM USER AS u " +
                            "INNER JOIN FRIENDS AS f ON u.ID = f.User_To " +
                            "WHERE f.User_From = ?;";
        try {
            return jdbcTemplate.query(sqlQuery, this::getUserDataFromQuery, userId);
        } catch (EmptyResultDataAccessException e) {
            log.info("Ошибка при чтении данных пользователя. Причина: {}", e.getMessage());
            throw new FilmorateNotFoundException("Пользователь с ID = " + userId + " не найден");
        } catch (DataAccessException e) {
            log.info("Ошибка при чтении данных пользователя. Причина: {}", e.getCause().getMessage());
            throw new FilmorateSqlException("Ошибка при чтении данных пользователя");
        }
    }

    @Override
    public List<User> getCommonFriendsList(Long userId, Long otherId)
    {
        String sqlQuery =   "SELECT * "
                          + "FROM USER AS u "
                          + "WHERE u.ID IN   ( "
                          + "                    SELECT f_0.USER_TO "
                          + "                    FROM FRIENDS AS f_0 "
                          + "                    INNER JOIN FRIENDS AS f_1 ON f_0.USER_TO = f_1.USER_TO "
                          + "                    WHERE (f_0.USER_FROM = ?) AND (f_1.USER_FROM = ?) "
                          + "                );";
        try {
            return jdbcTemplate.query(sqlQuery, this::getUserDataFromQuery, userId, otherId);
        } catch (EmptyResultDataAccessException e) {
            log.info("Ошибка при чтении данных пользователя. Причина: {}", e.getMessage());
            throw new FilmorateNotFoundException("Пользователь с ID = " + userId + " не найден");
        } catch (DataAccessException e) {
            log.info("Ошибка при чтении данных пользователя. Причина: {}", e.getCause().getMessage());
            throw new FilmorateSqlException("Ошибка при чтении данных пользователя");
        }
    }

    private User getUserDataFromQuery(ResultSet rs, int rowNum) throws SQLException {
        Long userId = rs.getLong("ID");
        Set<Long> friendsIdList = getFriendsIdList(userId);

        return User.builder()
                .id(userId)
                .login(rs.getString("Login"))
                .name(rs.getString("Name"))
                .email(rs.getString("Email"))
                .birthday(LocalDate.parse(rs.getString("Birthday"), DateTimeFormatter.ISO_DATE))
                .friends(friendsIdList)
                .build();
    }

    private void checkUserName(User user) {
        if ((user.getName() == null) || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private Set<Long> getFriendsIdList(Long userId) {
        String query = "SELECT User_To "
                + "FROM Friends "
                + "WHERE User_From = ?"
                + ";";
        try {
            return new HashSet<>(jdbcTemplate.query(query, this::friendsIdListFromQuery, userId));
        } catch (DataAccessException e) {
            return new HashSet<>();
        }
    }

    private Long friendsIdListFromQuery(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong("User_To");
    }

}
