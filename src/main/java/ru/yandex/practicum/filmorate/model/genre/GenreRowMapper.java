package ru.yandex.practicum.filmorate.model.genre;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreRowMapper implements RowMapper<Genre> {
    @Override
    public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("Genre_ID"))
                .name(rs.getString("Name"))
                .description(rs.getString("Description"))
                .build();
    }
}