package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.FilmorateSqlException;
import ru.yandex.practicum.filmorate.model.genre.Genre;
import ru.yandex.practicum.filmorate.model.genre.GenreRowMapper;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component("genreDbStorage")
@Repository
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getGenreList() {
        try {
            return jdbcTemplate.query("SELECT * FROM GENRE;", new GenreRowMapper());
        } catch (DataAccessException e) {
            log.info("Ошибка при чтении списка жанров. Причина: {}", e.getCause().getMessage());
            throw new FilmorateSqlException("Ошибка при чтении списка жанров");
        }
    }

    @Override
    public Genre getGenre(Long genreId) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM GENRE WHERE GenreID = ?;", new GenreRowMapper(), genreId);
        } catch (EmptyResultDataAccessException e) {
            log.info("Ошибка при чтении данных жанра с genreId = {}. Причина: {}", genreId, e.getMessage());
            throw new FilmorateNotFoundException("Ошибка при чтении данных жанра");
        } catch (DataAccessException e) {
            log.info("Ошибка при чтении данных жанра с genreId = {}. Причина: {}", genreId, e.getCause().getMessage());
            throw new FilmorateSqlException("Ошибка при чтении данных жанра");
        }
    }

    @Override
    public List<Genre> getGenreListByFilmId(Long filmId) {
        String query = "SELECT g.GenreID, g.Name, g.Description "
                + "FROM GENRE AS g "
                + "INNER JOIN FILMGENRES AS fg ON g.GenreID = fg.GENREID "
                + "WHERE fg.FILMID = ?"
                + ";";
        try {
            return jdbcTemplate.query(query, new GenreRowMapper(), filmId);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            log.info("Ошибка при чтении списка жанров фильма c filmId = {}. Причина: {}", filmId, e.getCause().getMessage());
            throw new FilmorateSqlException("Ошибка при чтении списка жанров фильма");
        }
    }
}
