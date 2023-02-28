package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private Long id;
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — не более 200 символов")
    private String description;

    private LocalDate  releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private Integer duration;

    private Set<Long> likesList;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Film film = (Film) o;

        if (!Objects.equals(name, film.name)) return false;
        if (!Objects.equals(description, film.description)) return false;
        if (!Objects.equals(releaseDate, film.releaseDate)) return false;
        if (!Objects.equals(duration, film.duration)) return false;
        return Objects.equals(likesList, film.likesList);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (releaseDate != null ? releaseDate.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        result = 31 * result + (likesList != null ? likesList.hashCode() : 0);
        return result;
    }
}
