package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.mpa.MPA;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/test_schema.sql", "/test_data.sql"})
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;

    @BeforeEach
    public void beforeEach() {

    }

    @Test
    public void getFilmsListTest() {
        List<Film> fl = filmStorage.getFilmsList();
        assertEquals(5, fl.size());
    }

    @Test
    public void getFilmTest() {
        Film f = filmStorage.getFilm(2L);
        assertEquals("О чём говорят мужчины", f.getName());
        assertEquals(93, f.getDuration());
    }

    @Test
    public void createFilmTest(){
        Film test_film = Film.builder()
                .id(null)
                .name("test_film_name_0")
                .description("test description 0")
                .genres(new ArrayList<>())
                .mpa(new MPA())
                .releaseDate(LocalDate.parse("1941-01-01", DateTimeFormatter.ISO_DATE))
                .duration(120)
                .likesCount(0)
                .likesList(new HashSet<>())
                .build();

        Film retFilm = filmStorage.createFilm(test_film);

        assertEquals("test_film_name_0", retFilm.getName());
    }

    @Test
    public void updateFilmTest() {
        Film ret_film_0 = filmStorage.getFilm(1L);

        Film test_film = Film.builder()
                .id(1L)
                .name("test_film_name_1")
                .description("test description 1")
                .genres(new ArrayList<>())
                .mpa(new MPA())
                .releaseDate(LocalDate.parse("1941-01-01", DateTimeFormatter.ISO_DATE))
                .duration(120)
                .likesCount(0)
                .likesList(new HashSet<>())
                .build();

        Film ret_film = filmStorage.updateFilm(test_film);

        assertEquals("Чебурашка", ret_film_0.getName());
        assertEquals(113, ret_film_0.getDuration());

        assertEquals("test_film_name_1", ret_film.getName());
        assertEquals(120, ret_film.getDuration());
    }

    @Test
    public void addLikeTest() {
        Film tf_0 = filmStorage.getFilm(3L);

        filmStorage.addLike(3L, 2L);
        filmStorage.addLike(3L, 4L);

        Film tf_1 = filmStorage.getFilm(3L);

        assertEquals(0, tf_0.getLikesList().size());

        assertEquals(2, tf_1.getLikesList().size());
        assertTrue(tf_1.getLikesList().contains(2L));
        assertTrue(tf_1.getLikesList().contains(4L));
    }

    @Test
    public void deleteLikeTest() {
        filmStorage.addLike(2L, 3L);
        filmStorage.addLike(2L, 4L);
        filmStorage.addLike(2L, 5L);

        Film tf_0 = filmStorage.getFilm(2L);

        filmStorage.deleteLike(2L,4L);

        Film tf_1 = filmStorage.getFilm(2L);

        assertEquals(3, tf_0.getLikesList().size());
        assertTrue(tf_0.getLikesList().contains(3L));
        assertTrue(tf_0.getLikesList().contains(4L));
        assertTrue(tf_0.getLikesList().contains(5L));

        assertEquals(2, tf_1.getLikesList().size());
        assertTrue(tf_1.getLikesList().contains(3L));
        assertTrue(tf_1.getLikesList().contains(5L));
    }
}