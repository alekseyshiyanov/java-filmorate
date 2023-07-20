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
            log.info("Ошибка при чтении данных списка пользователей. Причина: {}", e.getCause().getMessage());
            throw new FilmorateSqlException("Ошибка при чтении данных списка пользователей");
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

            return jdbcTemplate.queryForObject("SELECT * FROM USER ORDER BY User_ID DESC LIMIT 1;", this::getUserDataFromQuery);
        } catch (DataAccessException e) {
            log.info("Ошибка при добавлении нового пользователя. Причина: {}", e.getCause().getMessage());
            throw new FilmorateSqlException("Ошибка при добавлении нового пользователя");
        }
    }

    @Override
    public User updateUser(User user)
    {
        String sqlUpdateQuery = "UPDATE USER SET Login = ?, Name = ?, Email = ?, Birthday = ? WHERE User_ID = ?;";
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

            return jdbcTemplate.queryForObject("SELECT * FROM USER WHERE User_ID = ?;", this::getUserDataFromQuery, userId);
        } catch (DataAccessException e) {
            log.info("Ошибка при обновлении данных пользователя. Причина: {}", e.getCause().getMessage());
            throw new FilmorateSqlException("Ошибка при обновлении данных пользователя");
        }
    }

    @Override
    public User getUser(Long userId)
    {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM USER WHERE User_ID = ?;", this::getUserDataFromQuery, userId);
        } catch (EmptyResultDataAccessException e) {
            log.info("Ошибка при чтении данных пользователя. Причина: {}", e.getMessage());
            throw new FilmorateNotFoundException("Пользователь с ID = " + userId + " не найден");
        } catch (DataAccessException e) {
            log.info("Ошибка при чтении данных пользователя. Причина: {}", e.getCause().getMessage());
            throw new FilmorateSqlException("Ошибка при чтении данных пользователя");
        }
    }

    @Override
    public List<User> getFriendsList(Long userId)
    {
        String sqlQuery =   "SELECT u.* " +
                            "FROM USER AS u, FRIENDS f " +
                            "WHERE (u.User_ID = f.User_To) " +
                            "AND (f.User_From = ?);";
        try {
            return jdbcTemplate.query(sqlQuery, this::getUserDataFromQuery, userId);
        } catch (EmptyResultDataAccessException e) {
            log.info("Ошибка при чтении данных списка друзей пользователя. Причина: {}", e.getMessage());
            throw new FilmorateNotFoundException("Пользователь с ID = " + userId + " не найден");
        } catch (DataAccessException e) {
            log.info("Ошибка при чтении данных списка друзей пользователя. Причина: {}", e.getCause().getMessage());
            throw new FilmorateSqlException("Ошибка при чтении данных списка друзей пользователя");
        }
    }

    @Override
    public List<User> getCommonFriendsList(Long userId, Long otherId)
    {
        String sqlQuery =     "SELECT u.* "
                            + "FROM USER u, FRIENDS f_0, FRIENDS f_1 "
                            + "WHERE (f_0.USER_TO = f_1.USER_TO) "
                            + "AND (u.User_ID = f_0.USER_TO) "
                            + "AND (f_0.USER_FROM = ?) "
                            + "AND (f_1.USER_FROM = ?);";
        try {
            return jdbcTemplate.query(sqlQuery, this::getUserDataFromQuery, userId, otherId);
        } catch (EmptyResultDataAccessException e) {
            log.info("Ошибка при чтении данных списка общих друзей пользователя с userId = {} и пользователя с otherId = {}. Причина: {}",
                    userId, otherId, e.getMessage());
            throw new FilmorateNotFoundException("Пользователь с ID = " + userId + " не найден");
        } catch (DataAccessException e) {
            log.info("Ошибка при чтении данных списка общих друзей пользователя с userId = {} и пользователя с otherId = {}. Причина: {}",
                    userId, otherId, e.getCause().getMessage());
            throw new FilmorateSqlException("Ошибка при чтении данных списка общих друзей пользователя");
        }
    }

    private User getUserDataFromQuery(ResultSet rs, int rowNum) throws SQLException {
        Long userId = rs.getLong("User_ID");
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
        String query = "SELECT User_To FROM Friends WHERE User_From = ?;";
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
