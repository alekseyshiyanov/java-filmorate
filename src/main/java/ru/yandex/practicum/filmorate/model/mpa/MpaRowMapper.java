package ru.yandex.practicum.filmorate.model.mpa;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MpaRowMapper implements RowMapper<MPA> {
    @Override
    public MPA mapRow(ResultSet rs, int rowNum) throws SQLException {
        return MPA.builder()
                .id(rs.getInt("ID"))
                .name(rs.getString("Name"))
                .description(rs.getString("Description"))
                .build();
    }
}
