package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.genre.Genre;
import ru.yandex.practicum.filmorate.model.mpa.MPA;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private Long id;
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — не более 200 символов")
    private String description;

    private List<Genre> genres;

    private MPA mpa;

    private LocalDate  releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private Integer duration;

    @Positive(message = "Количество лайков должно быть положительным")
    private Integer likesCount;

    private Set<Long> likesList;

    public void setGenres(List<Genre> genres) {
        this.genres = Objects.isNull(genres) ? new ArrayList<>() : genres;
    }

    public void setMpa(MPA mpa) {
        this.mpa = Objects.isNull(mpa) ? new MPA() : mpa;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = Objects.isNull(likesCount) ? 0 : likesCount;
    }

    public Integer getLikesCount() {
        likesCount = Objects.isNull(likesCount) ? 0 : likesCount;
        return likesCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Film film = (Film) o;

        if (!Objects.equals(name, film.name)) return false;
        if (!Objects.equals(description, film.description)) return false;
        if (!Objects.equals(releaseDate, film.releaseDate)) return false;
        if (!Objects.equals(duration, film.duration)) return false;
        if (!Objects.equals(likesCount, film.likesCount)) return false;
        return Objects.equals(likesList, film.likesList);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (releaseDate != null ? releaseDate.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        result = 31 * result + (likesCount != null ? likesCount.hashCode() : 0);
        result = 31 * result + (likesList != null ? likesList.hashCode() : 0);
        return result;
    }
}
