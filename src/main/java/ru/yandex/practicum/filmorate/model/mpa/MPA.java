package ru.yandex.practicum.filmorate.model.mpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MPA {
    private Integer id;
    private String name;
    private String description;
}
