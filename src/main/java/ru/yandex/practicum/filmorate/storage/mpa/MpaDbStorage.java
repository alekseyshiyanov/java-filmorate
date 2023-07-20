package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.FilmorateSqlException;
import ru.yandex.practicum.filmorate.model.mpa.MPA;
import ru.yandex.practicum.filmorate.model.mpa.MpaRowMapper;

import java.util.List;

@Slf4j
@Component("mpaDbStorage")
@Repository
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MPA> getMpaList() {
        try {
            return jdbcTemplate.query("SELECT * FROM MPA;", new MpaRowMapper());
        } catch (DataAccessException e) {
            log.info("Ошибка при чтении списка рейтингов MPA. Причина: {}", e.getCause().getMessage());
            throw new FilmorateSqlException("Ошибка при чтении списка рейтингов MPA");
        }
    }

    @Override
    public MPA getMpa(Long mpaId) {
        try {
            if (mpaId > 0) {
                return jdbcTemplate.queryForObject("SELECT * FROM MPA WHERE Mpa_Id = ?;", new MpaRowMapper(), mpaId);
            }
            log.info("mpaRatingIndex <= 0");
            return null;
        } catch (EmptyResultDataAccessException e) {
            log.info("Ошибка при чтении данных рейтинга MPA. Причина: {}", e.getMessage());
            throw new FilmorateNotFoundException("Ошибка при обновлении данных рейтинга MPA");
        } catch (DataAccessException e) {
            log.info("Ошибка при чтении данных рейтинга MPA. Причина: {}", e.getCause().getMessage());
            throw new FilmorateSqlException("Ошибка при чтении данных рейтинга MPA");
        }
    }
}
