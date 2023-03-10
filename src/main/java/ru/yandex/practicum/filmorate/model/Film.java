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

        if (getName() != null ? !getName().equals(film.getName()) : film.getName() != null) return false;
        if (getDescription() != null ? !getDescription().equals(film.getDescription()) : film.getDescription() != null)
            return false;
        if (getGenres() != null ? !getGenres().equals(film.getGenres()) : film.getGenres() != null) return false;
        if (getMpa() != null ? !getMpa().equals(film.getMpa()) : film.getMpa() != null) return false;
        if (getReleaseDate() != null ? !getReleaseDate().equals(film.getReleaseDate()) : film.getReleaseDate() != null)
            return false;
        if (getDuration() != null ? !getDuration().equals(film.getDuration()) : film.getDuration() != null)
            return false;
        if (getLikesCount() != null ? !getLikesCount().equals(film.getLikesCount()) : film.getLikesCount() != null)
            return false;
        return getLikesList() != null ? getLikesList().equals(film.getLikesList()) : film.getLikesList() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getGenres() != null ? getGenres().hashCode() : 0);
        result = 31 * result + (getMpa() != null ? getMpa().hashCode() : 0);
        result = 31 * result + (getReleaseDate() != null ? getReleaseDate().hashCode() : 0);
        result = 31 * result + (getDuration() != null ? getDuration().hashCode() : 0);
        result = 31 * result + (getLikesCount() != null ? getLikesCount().hashCode() : 0);
        result = 31 * result + (getLikesList() != null ? getLikesList().hashCode() : 0);
        return result;
    }
}
