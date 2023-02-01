package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String url;

    @BeforeEach
    void setUp() {
        url = "http://localhost:" + port + "/films";
    }

    @Test
    void getFilmsListStandardBehavior() {
        Film testFilm1 = new Film(null, "test_film_name_1", "test description 1", LocalDate.parse("1941-01-01", DateTimeFormatter.ISO_DATE), 120);
        Film testFilm2 = new Film(null, "test_film_name_2", "test description 2", LocalDate.parse("1942-01-01", DateTimeFormatter.ISO_DATE), 120);

        Film retFilm1 = restTemplate.postForObject(url, testFilm1, Film.class);
        Film retFilm2 = restTemplate.postForObject(url, testFilm2, Film.class);

        GetFilmsListResponse actualResponse = getFilmsList(url);

        assertEquals(200, actualResponse.statusCode);

        assertFalse(actualResponse.filmsList.isEmpty());
        assertTrue(actualResponse.filmsList.contains(testFilm1));
        assertTrue(actualResponse.filmsList.contains(testFilm2));
    }

    @Test
    void createFilmStandardBehavior() {
        Film testFilm3 = new Film(null, "test_film_name_3", "test description 3", LocalDate.parse("1943-01-01", DateTimeFormatter.ISO_DATE), 120);
        Film testFilm4 = new Film(null, "test_film_name_4", "test description 4", LocalDate.parse("1944-01-01", DateTimeFormatter.ISO_DATE), 120);

        Film retFilm3 = restTemplate.postForObject(url, testFilm3, Film.class);
        assertEquals(testFilm3, retFilm3);

        Film retFilm4 = restTemplate.postForObject(url, testFilm4, Film.class);
        assertEquals(testFilm4, retFilm4);

        GetFilmsListResponse actualResponse = getFilmsList(url);

        assertEquals(200, actualResponse.statusCode);

        assertFalse(actualResponse.filmsList.isEmpty());
        assertTrue(actualResponse.filmsList.contains(testFilm3));
        assertTrue(actualResponse.filmsList.contains(testFilm4));
    }

    @Test
    void updateFilmStandardBehavior() {
        Film testFilm5 = new Film(null, "test_film_name_5", "test description 5", LocalDate.parse("1945-01-01", DateTimeFormatter.ISO_DATE), 120);
        Film testFilm6 = new Film(null, "test_film_name_6", "test description 6", LocalDate.parse("1946-01-01", DateTimeFormatter.ISO_DATE), 120);

        Film retFilm5 = restTemplate.postForObject(url, testFilm5, Film.class);
        Film retFilm6 = restTemplate.postForObject(url, testFilm6, Film.class);

        Film testFilm7 = new Film(retFilm5.getId(), "test_film_name_7", "test description 7", LocalDate.parse("1947-01-01", DateTimeFormatter.ISO_DATE), 120);

        restTemplate.put(url, testFilm7, Film.class);

        GetFilmsListResponse actualResponse = getFilmsList(url);

        assertEquals(200, actualResponse.statusCode);

        assertFalse(actualResponse.filmsList.isEmpty());
        assertFalse(actualResponse.filmsList.contains(retFilm5));
        assertTrue(actualResponse.filmsList.contains(retFilm6));
        assertTrue(actualResponse.filmsList.contains(testFilm7));
    }

    @Test
    void updateFilmWithWrongUid() {
        Film testFilm8 = new Film(null, "test_film_name_8", "test description 8", LocalDate.parse("1948-01-01", DateTimeFormatter.ISO_DATE), 120);
        Film testFilm9 = new Film(null, "test_film_name_9", "test description 9", LocalDate.parse("1949-01-01", DateTimeFormatter.ISO_DATE), 120);

        Film retFilm8 = restTemplate.postForObject(url, testFilm8, Film.class);
        Film retFilm9 = restTemplate.postForObject(url, testFilm9, Film.class);

        Film testFilm10 = new Film(100L, "test_film_name_10", "test description 10", LocalDate.parse("1950-01-01", DateTimeFormatter.ISO_DATE), 120);

        HttpEntity<Film> request = new HttpEntity<>(testFilm10);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

        assertEquals(404, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\n\t\"Ошибка обновления объекта. Объект с ID = 100 не существует\"\n]", actualResponseEntity.getBody());
    }

    @Test
    void updateFilmWithNullUid() {
        Film testFilm11 = new Film(null, "test_film_name_11", "test description 11", LocalDate.parse("1951-01-01", DateTimeFormatter.ISO_DATE), 120);
        Film testFilm12 = new Film(null, "test_film_name_12", "test description 12", LocalDate.parse("1952-01-01", DateTimeFormatter.ISO_DATE), 120);

        Film retFilm11 = restTemplate.postForObject(url, testFilm11, Film.class);
        Film retFilm12 = restTemplate.postForObject(url, testFilm12, Film.class);

        Film testFilm13 = new Film(null, "test_film_name_13", "test description 13", LocalDate.parse("1953-01-01", DateTimeFormatter.ISO_DATE), 120);

        HttpEntity<Film> request = new HttpEntity<>(testFilm13);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\n\t\"Ошибка обновления объекта. ID не должен быть null\"\n]", actualResponseEntity.getBody());
    }

    @Test
    void updateFilmWithNegativeUid() {
        Film testFilm14 = new Film(null, "test_film_name_14", "test description 14", LocalDate.parse("1954-01-01", DateTimeFormatter.ISO_DATE), 120);
        Film testFilm15 = new Film(null, "test_film_name_15", "test description 15", LocalDate.parse("1955-01-01", DateTimeFormatter.ISO_DATE), 120);

        Film retFilm14 = restTemplate.postForObject(url, testFilm14, Film.class);
        Film retFilm15 = restTemplate.postForObject(url, testFilm15, Film.class);

        Film testFilm16 = new Film(-1L, "test_film_name_16", "test description 16", LocalDate.parse("1956-01-01", DateTimeFormatter.ISO_DATE), 120);

        HttpEntity<Film> request = new HttpEntity<>(testFilm16);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\n\t\"Ошибка обновления объекта. ID должен быть положительным числом\"\n]", actualResponseEntity.getBody());
    }

    @Test
    void createFilmWithNullReleaseDate() {
        Film testFilm17 = new Film(null, "test_film_name_17", "test description 17", null, 120);

        HttpEntity<Film> request = new HttpEntity<>(testFilm17);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\n\t\"Дата релиза не может быть null\"\n]", actualResponseEntity.getBody());
    }

    @Test
    void createFilmWithToEarlyReleaseDate() {
        Film testFilm18 = new Film(null, "test_film_name_18", "test description 18", LocalDate.parse("1856-01-01", DateTimeFormatter.ISO_DATE), 120);

        HttpEntity<Film> request = new HttpEntity<>(testFilm18);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\n\t\"Дата релиза не может быть ранее 1895-12-28\"\n]", actualResponseEntity.getBody());
    }

    @Test
    void createFilmWithEmptyName() {
        Film testFilm19 = new Film(null, "", "test description 19", LocalDate.parse("1959-01-01", DateTimeFormatter.ISO_DATE), 120);

        HttpEntity<Film> request = new HttpEntity<>(testFilm19);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Название фильма не может быть пустым\"]", actualResponseEntity.getBody());
    }

    @Test
    void createFilmWithNullName() {
        Film testFilm20 = new Film(null, null, "test description 20", LocalDate.parse("1960-01-01", DateTimeFormatter.ISO_DATE), 120);

        HttpEntity<Film> request = new HttpEntity<>(testFilm20);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Название фильма не может быть пустым\"]", actualResponseEntity.getBody());
    }

    @Test
    void createFilmWithNegativeDuration() {
        Film testFilm21 = new Film(null, "test_film_name_21", "test description 21", LocalDate.parse("1960-01-01", DateTimeFormatter.ISO_DATE), -1);

        HttpEntity<Film> request = new HttpEntity<>(testFilm21);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Продолжительность фильма должна быть положительной\"]", actualResponseEntity.getBody());
    }

    private GetFilmsListResponse getFilmsList (String url) {
        GetFilmsListResponse result = new GetFilmsListResponse();

        ResponseEntity<ArrayList<Film>> actualResponseEntity =
                restTemplate.exchange(url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {});

        result.filmsList = actualResponseEntity.getBody();
        result.statusCode = actualResponseEntity.getStatusCodeValue();

        return result;
    }

    static class GetFilmsListResponse {
        public int statusCode;
        public ArrayList<Film> filmsList;
    }

}