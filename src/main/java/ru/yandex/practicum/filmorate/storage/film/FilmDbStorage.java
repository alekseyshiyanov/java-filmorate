package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.FilmorateSqlException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.genre.Genre;
import ru.yandex.practicum.filmorate.model.mpa.MPA;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component("filmDbStorage")
@Repository
public class FilmDbStorage implements FilmStorage {

    private final String CREATE_NEW_FILM_QUERY = "INSERT INTO FILM (Name, MPA_Rating, Description, ReleaseDate, Duration, LikesCount) "
                                               + "VALUES (?, ?, ?, ?, ?, ?);";
    private final String GET_ALL_FILMS_QUERY = "SELECT * FROM FILM;";
    private final String INSERT_FILM_GENRE_QUERY = "INSERT INTO FilmGenres (FilmID, GenreID) "
            + "VALUES (?, ?) ON DUPLICATE KEY UPDATE FilmID = FilmID, GenreID = GenreID;";
    private final String DELETE_ALL_FILM_GENRE_QUERY = "DELETE FROM FilmGenres WHERE FilmID = ?;";
    private final String INC_LIKE_COUNTER_QUERY = "UPDATE FILM SET LikesCount=LikesCount+1 WHERE ID = ?;";
    private final String DEC_LIKE_COUNTER_QUERY = "UPDATE FILM SET LikesCount=LikesCount-1 WHERE (ID = ?) AND (LikesCount > 0);";
    private final String ADD_LIKE_QUERY = "INSERT INTO FilmLikes (UserID, FilmID) "
                                        + "VALUES (?, ?) ON DUPLICATE KEY UPDATE UserID = UserID, FilmID = FilmID;";
    private final String DELETE_LIKE_QUERY = "DELETE FROM FilmLikes WHERE (UserID = ?) AND (FilmID = ?);";
    private final String SELECT_FILM_BY_ID_QUERY = "SELECT * FROM FILM WHERE ID = ?;";
//    private final String GET_LAST_INSERTED_ID_QUERY = "SELECT LAST_INSERT_ID();";
    private final String GET_LAST_INSERTED_ID_QUERY = "SELECT ID FROM FILM ORDER BY ID DESC LIMIT 1;";

    @Autowired
    @Qualifier("genreDbStorage")
    private GenreStorage genreStorage;

    @Autowired
    @Qualifier("mpaDbStorage")
    private MpaStorage mpaStorage;

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getFilmsList()
    {
        try {
            return jdbcTemplate.query(GET_ALL_FILMS_QUERY, this::getFilmDataFromQuery);
        } catch (DataAccessException e) {
            log.info("Ошибка при чтении списка фильмов. Причина: {}", e.getCause().getMessage());
            throw new FilmorateSqlException("Ошибка при чтении списка фильмов");
        }
    }

    @Override
    public Film createFilm(Film film)
    {
        try {
            return createFilmInDb(CREATE_NEW_FILM_QUERY, film);
        } catch (DataAccessException e) {
            log.info("Ошибка при добавлении нового фильма. Причина: {}", e.getCause().getMessage());
            throw new FilmorateSqlException("Ошибка при добавлении нового фильма");
        }
    }

    @Override
    public Film updateFilm(Film film)
    {
        String sqlUpdateQuery = "UPDATE FILM "
                + "SET Name = ?, MPA_Rating = ?, Description = ?, ReleaseDate = ?, Duration = ?, LikesCount = ? "
                + "WHERE ID = ?;";

        try {
            Long filmId = film.getId();

            jdbcTemplate.update(sqlUpdateQuery,
                    film.getName(),
                    (film.getMpa() != null) ? film.getMpa().getId() : null,
                    film.getDescription(),
                    film.getReleaseDate().format(DateTimeFormatter.ISO_DATE),
                    film.getDuration(),
                    film.getLikesCount(),
                    filmId);

            insertFilmGenres(film.getGenres(), filmId);

            return getFilm(filmId);
        } catch (EmptyResultDataAccessException e) {
            log.info("Ошибка при обновлении данных фильма. Причина: {}", e.getMessage());
            throw new FilmorateNotFoundException("Ошибка при обновлении данных фильма");
        } catch (DataAccessException e) {
            log.info("Ошибка при обновлении данных фильма. Причина: {}", e.getCause().getMessage());
            throw new FilmorateSqlException("Ошибка при обновлении данных фильма");
        }
    }

    @Override
    public Film getFilm(Long filmId)
    {
        try {
            return jdbcTemplate.queryForObject(SELECT_FILM_BY_ID_QUERY, this::getFilmDataFromQuery, filmId);
        } catch (EmptyResultDataAccessException e) {
            log.info("Ошибка при чтении данных фильма. Причина: {}", e.getMessage());
            throw new FilmorateNotFoundException(String.format("Фильм с ID = %d не найден", filmId));
        } catch (DataAccessException e) {
            log.info("Ошибка при чтении данных фильма. Причина: {}", e.getCause().getMessage());
            throw new FilmorateSqlException(String.format("Ошибка при чтении данных фильма с ID = %d", filmId));
        }
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

    @Override
    public List<Film> likedFilmsList(Long count)
    {
//        String sqlQuery =   "SELECT * "
//                          + "FROM FILM f "
//                          + "WHERE id IN ( "
//                          + "               SELECT f2.FILMID "
//                          + "               FROM FILMLIKES f2 "
//                          + "               GROUP BY f2.FILMID "
//                          + "               ORDER BY count(f2.FILMID) DESC "
//                          + "               LIMIT ?"
//                          + "             );";
        String sqlQuery =   "SELECT * "
                          + "FROM FILM "
                          + "GROUP BY ID "
                          + "ORDER BY LIKESCOUNT DESC "
                          + "LIMIT ?;";
        try {
            return jdbcTemplate.query(sqlQuery, this::getFilmDataFromQuery, count);
        } catch (DataAccessException e) {
            log.info("Ошибка при чтении списка самых популярных фильмов. Причина: {}", e.getCause().getMessage());
            throw new FilmorateSqlException("Ошибка при чтении списка самых популярных фильмов");
        }
    }

    private Film getFilmDataFromQuery(ResultSet rs, int rowNum) throws SQLException {
        MPA mpa = mpaStorage.getMpa(rs.getLong("MPA_Rating"));

        Long filmId = rs.getLong("ID");
        List<Genre> genre = genreStorage.getGenreListByFilmId(filmId);
        Set<Long> likesList = getLikesListByFilmId(filmId);

        return  Film.builder()
                .id(filmId)
                .name(rs.getString("Name"))
                .genres(genre)
                .mpa(mpa)
                .description(rs.getString("Description"))
                .releaseDate(LocalDate.parse(rs.getString("ReleaseDate"), DateTimeFormatter.ISO_DATE))
                .duration(rs.getInt("Duration"))
                .likesCount(rs.getInt("LikesCount"))
                .likesList(likesList)
                .build();
    }

    private Film createFilmInDb(String query, Film film) {
        jdbcTemplate.update(query,
                            film.getName(),
                            (film.getMpa() != null) ? film.getMpa().getId() : null,
                            film.getDescription(),
                            film.getReleaseDate().format(DateTimeFormatter.ISO_DATE),
                            film.getDuration(),
                            0);

        Long lastId =  jdbcTemplate.queryForObject(GET_LAST_INSERTED_ID_QUERY, Long.class);

        var fg = film.getGenres();

        insertFilmGenres(fg, lastId);

        return getFilm(lastId);
    }

    private int[] insertFilmGenres(List<Genre> listGenre, Long filmId) {
        if (Objects.isNull(listGenre) || Objects.isNull(filmId)) {
            return new int[]{0};
        }

        jdbcTemplate.update(DELETE_ALL_FILM_GENRE_QUERY, filmId);

        List<Object[]> batch = new ArrayList<>();
        for (Genre g : listGenre) {
            Object[] values = new Object[] {filmId, g.getId()};
            batch.add(values);
        }
        return this.jdbcTemplate.batchUpdate(INSERT_FILM_GENRE_QUERY, batch);
    }

    private Set<Long> getLikesListByFilmId(Long filmId) {
        String query = "SELECT UserID "
                + "FROM FilmLikes "
                + "WHERE FilmID = ?"
                + ";";
        try {
            return new HashSet<>(jdbcTemplate.query(query, this::likesListFromQuery, filmId));
        } catch (DataAccessException e) {
            return new HashSet<>();
        }
    }

    private Long likesListFromQuery(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong("UserID");
    }
}
