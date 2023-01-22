package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import ru.yandex.practicum.filmorate.utils.deserializers.FilmDeserializer;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = FilmDeserializer.class)
public class Film {
    private Long id;
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(min = 0, max = 200, message = "Максимальная длина описания — не более 200 символов")
    private String description;

    private LocalDate  releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private Integer duration;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Film)) return false;

        Film film = (Film) o;

        if (getName() != null ? !getName().equals(film.getName()) : film.getName() != null) return false;
        if (getDescription() != null ? !getDescription().equals(film.getDescription()) : film.getDescription() != null)
            return false;
        if (getReleaseDate() != null ? !getReleaseDate().equals(film.getReleaseDate()) : film.getReleaseDate() != null)
            return false;
        return getDuration() != null ? getDuration().equals(film.getDuration()) : film.getDuration() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getReleaseDate() != null ? getReleaseDate().hashCode() : 0);
        result = 31 * result + (getDuration() != null ? getDuration().hashCode() : 0);
        return result;
    }
}
