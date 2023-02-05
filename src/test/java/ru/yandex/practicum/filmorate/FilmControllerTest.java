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
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String url;

    private int testNumber = 1;

    @BeforeEach
    void setUp() {
        url = "http://localhost:" + port + "/films";
    }

    @Test
    @DirtiesContext
    void getFilmsListStandardBehavior() {
        Film testFilm1 = createNewFilm();
        Film testFilm2 = createNewFilm();

        restTemplate.postForObject(url, testFilm1, Film.class);
        restTemplate.postForObject(url, testFilm2, Film.class);

        GetFilmsListResponse actualResponse = getFilmsList(url);

        assertEquals(200, actualResponse.statusCode);

        assertFalse(actualResponse.filmsList.isEmpty());
        assertTrue(actualResponse.filmsList.contains(testFilm1));
        assertTrue(actualResponse.filmsList.contains(testFilm2));
    }

    @Test
    @DirtiesContext
    void createFilmStandardBehavior() {
        Film testFilm1 = createNewFilm();
        Film testFilm2 = createNewFilm();

        Film retFilm1 = restTemplate.postForObject(url, testFilm1, Film.class);
        assertEquals(testFilm1, retFilm1);

        Film retFilm2 = restTemplate.postForObject(url, testFilm2, Film.class);
        assertEquals(testFilm2, retFilm2);

        GetFilmsListResponse actualResponse = getFilmsList(url);

        assertEquals(200, actualResponse.statusCode);

        assertFalse(actualResponse.filmsList.isEmpty());
        assertTrue(actualResponse.filmsList.contains(testFilm1));
        assertTrue(actualResponse.filmsList.contains(testFilm2));
    }

    @Test
    @DirtiesContext
    void createFilmWithNullLikeListBehavior() {
        Film testFilm1 = new Film(null, "test_film_name_" + testNumber, "test description " + (testNumber++), LocalDate.parse("1941-01-01", DateTimeFormatter.ISO_DATE), 120, null);
        Film testFilm2 = new Film(null, "test_film_name_" + testNumber, "test description " + (testNumber++), LocalDate.parse("1941-01-01", DateTimeFormatter.ISO_DATE), 120, null);

        restTemplate.postForObject(url, testFilm1, Film.class);
        restTemplate.postForObject(url, testFilm2, Film.class);

        GetFilmsListResponse actualResponse = getFilmsList(url);

        assertEquals(200, actualResponse.statusCode);

        for (Film film : actualResponse.filmsList) {
            assertNotNull(film.getLikesList());
        }
    }

    @Test
    @DirtiesContext
    void updateFilmStandardBehavior() {
        Film testFilm1 = createNewFilm();
        Film testFilm2 = createNewFilm();

        Film retFilm1 = restTemplate.postForObject(url, testFilm1, Film.class);
        Film retFilm2 = restTemplate.postForObject(url, testFilm2, Film.class);

        Film testFilm3 = new Film(retFilm1.getId(), "test_film_name_7", "test description 7", LocalDate.parse("1947-01-01", DateTimeFormatter.ISO_DATE), 120, new HashSet<>());

        restTemplate.put(url, testFilm3, Film.class);

        GetFilmsListResponse actualResponse = getFilmsList(url);

        assertEquals(200, actualResponse.statusCode);

        assertFalse(actualResponse.filmsList.isEmpty());
        assertFalse(actualResponse.filmsList.contains(retFilm1));
        assertTrue(actualResponse.filmsList.contains(retFilm2));
        assertTrue(actualResponse.filmsList.contains(testFilm3));
    }

    @Test
    @DirtiesContext
    void updateFilmWithWrongUid() {
        Film testFilm1 = createNewFilm();
        Film testFilm2 = createNewFilm();

        restTemplate.postForObject(url, testFilm1, Film.class);
        restTemplate.postForObject(url, testFilm2, Film.class);

        Film testFilm3 = new Film(100L, "test_film_name_10", "test description 10", LocalDate.parse("1950-01-01", DateTimeFormatter.ISO_DATE), 120, new HashSet<>());

        HttpEntity<Film> request = new HttpEntity<>(testFilm3);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

        assertEquals(404, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Ошибка обновления объекта. Объект с ID = 100 не существует\"]", actualResponseEntity.getBody());
    }

    @Test
    @DirtiesContext
    void updateFilmWithNullUid() {
        Film testFilm1 = createNewFilm();
        Film testFilm2 = createNewFilm();

        restTemplate.postForObject(url, testFilm1, Film.class);
        restTemplate.postForObject(url, testFilm2, Film.class);

        Film testFilm13 = new Film(null, "test_film_name_13", "test description 13", LocalDate.parse("1953-01-01", DateTimeFormatter.ISO_DATE), 120, new HashSet<>());

        HttpEntity<Film> request = new HttpEntity<>(testFilm13);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Ошибка обновления объекта. ID не должен быть null\"]", actualResponseEntity.getBody());
    }

    @Test
    @DirtiesContext
    void updateFilmWithNegativeUid() {
        Film testFilm1 = createNewFilm();
        Film testFilm2 = createNewFilm();

        restTemplate.postForObject(url, testFilm1, Film.class);
        restTemplate.postForObject(url, testFilm2, Film.class);

        Film testFilm16 = new Film(-1L, "test_film_name_16", "test description 16", LocalDate.parse("1956-01-01", DateTimeFormatter.ISO_DATE), 120, new HashSet<>());

        HttpEntity<Film> request = new HttpEntity<>(testFilm16);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Ошибка обновления объекта. ID должен быть положительным числом\"]", actualResponseEntity.getBody());
    }

    @Test
    @DirtiesContext
    void createFilmWithNullReleaseDate() {
        Film testFilm1 = new Film(null, "test_film_name_17", "test description 17", null, 120, new HashSet<>());

        HttpEntity<Film> request = new HttpEntity<>(testFilm1);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Дата релиза не может быть null\"]", actualResponseEntity.getBody());
    }

    @Test
    @DirtiesContext
    void createFilmWithToEarlyReleaseDate() {
        Film testFilm1 = new Film(null, "test_film_name_18", "test description 18", LocalDate.parse("1856-01-01", DateTimeFormatter.ISO_DATE), 120, new HashSet<>());

        HttpEntity<Film> request = new HttpEntity<>(testFilm1);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Дата релиза не может быть ранее 1895-12-28\"]", actualResponseEntity.getBody());
    }

    @Test
    @DirtiesContext
   void createFilmWithEmptyName() {
        Film testFilm1 = new Film(null, "", "test description 19", LocalDate.parse("1956-01-01", DateTimeFormatter.ISO_DATE), 120, new HashSet<>());

        HttpEntity<Film> request = new HttpEntity<>(testFilm1);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Название фильма не может быть пустым\"]", actualResponseEntity.getBody());
    }

    @Test
    @DirtiesContext
    void createFilmWithNullName() {
        Film testFilm1 = new Film(null, null, "test description 20", LocalDate.parse("1956-01-01", DateTimeFormatter.ISO_DATE), 120, new HashSet<>());

        HttpEntity<Film> request = new HttpEntity<>(testFilm1);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Название фильма не может быть пустым\"]", actualResponseEntity.getBody());
    }

    @Test
    @DirtiesContext
    void createFilmWithNegativeDuration() {
        Film testFilm1 = new Film(null, "test_film_name_21", "test description 21", LocalDate.parse("1956-01-01", DateTimeFormatter.ISO_DATE), -120, new HashSet<>());

        HttpEntity<Film> request = new HttpEntity<>(testFilm1);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Продолжительность фильма должна быть положительной\"]", actualResponseEntity.getBody());
    }

    @Test
    @DirtiesContext
    void addLikeStandardBehavior() {
        Film testFilm1 = createNewFilm();

        Film retFilm1 = restTemplate.postForObject(url, testFilm1, Film.class);

        ResponseEntity<Film> actualResponseEntity = restTemplate.exchange(url + "/" + retFilm1.getId() + "/like/111", HttpMethod.PUT, null, Film.class);

        assertEquals(200, actualResponseEntity.getStatusCodeValue());
        assertTrue(actualResponseEntity.getBody().getLikesList().contains(111L));
    }

    @Test
    @DirtiesContext
    void addLikeWithWrongFilmUidBehavior() {
        Film testFilm1 = createNewFilm();

        Film retFilm1 = restTemplate.postForObject(url, testFilm1, Film.class);

        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url + "/" + (retFilm1.getId() + 1000L) + "/like/111", HttpMethod.PUT, null, String.class);

        assertEquals(404, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Ошибка добавления лайка. Фильм с ID = " + (retFilm1.getId() + 1000L) + " не существует\"]", actualResponseEntity.getBody());
    }

    @Test
    @DirtiesContext
    void addLikeWithNullFilmUidBehavior() {
        Film testFilm1 = createNewFilm();

        Film retFilm1 = restTemplate.postForObject(url, testFilm1, Film.class);

        retFilm1.setId(null);

        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url + "/" + retFilm1.getId() + "/like/111", HttpMethod.PUT, null, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Параметр 'id' должен быть числом\"]", actualResponseEntity.getBody());
    }

    @Test
    @DirtiesContext
    void addLikeWithEmptyFilmUidBehavior() {
        Film testFilm1 = createNewFilm();

        restTemplate.postForObject(url, testFilm1, Film.class);

        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url + "/"  + "/like/111", HttpMethod.PUT, null, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Пропущен обязательный параметр 'id'\"]", actualResponseEntity.getBody());
    }

    @Test
    @DirtiesContext
    void addLikeWithNegativeFilmUidBehavior() {
        Film testFilm1 = createNewFilm();

        Film retFilm1= restTemplate.postForObject(url, testFilm1, Film.class);

        retFilm1.setId(-1L);

        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url + "/" + retFilm1.getId() + "/like/111", HttpMethod.PUT, null, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Параметр 'id' не может быть отрицательным\"]", actualResponseEntity.getBody());
    }

    @Test
    @DirtiesContext
    void addLikeWithNullUserIdBehavior() {
        Film testFilm1 = createNewFilm();

        Film retFilm1 = restTemplate.postForObject(url, testFilm1, Film.class);

        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url + "/" + retFilm1.getId() + "/like/null", HttpMethod.PUT, null, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Параметр 'userId' должен быть числом\"]", actualResponseEntity.getBody());
    }

    @Test
    @DirtiesContext
    void addLikeWithEmptyUserIdBehavior() {
        Film testFilm1 = createNewFilm();

        Film retFilm1 = restTemplate.postForObject(url, testFilm1, Film.class);

        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url + "/" + retFilm1.getId() + "/like/", HttpMethod.PUT, null, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Пропущен обязательный параметр 'userId'\"]", actualResponseEntity.getBody());
    }

    @Test
    @DirtiesContext
    void addLikeWithNegativeUserIdBehavior() {
        Film testFilm1 = createNewFilm();

        Film retFilm1 = restTemplate.postForObject(url, testFilm1, Film.class);

        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url + "/" + retFilm1.getId() + "/like/-1", HttpMethod.PUT, null, String.class);

        assertEquals(404, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Пользователь с userId = -1 не существует\"]", actualResponseEntity.getBody());
    }

    @Test
    @DirtiesContext
    void deleteLikeStandardIdBehavior() {
        Film testFilm1 = createNewFilm();

        Film retFilm1 = restTemplate.postForObject(url, testFilm1, Film.class);

        restTemplate.exchange(url + "/" + retFilm1.getId() + "/like/10", HttpMethod.PUT, null, Film.class);
        restTemplate.exchange(url + "/" + retFilm1.getId() + "/like/20", HttpMethod.PUT, null, Film.class);

        ResponseEntity<Film> actualResponseEntity_2 = restTemplate.exchange(url + "/" + retFilm1.getId(), HttpMethod.GET, null, Film.class);

        Film retFilm2 = actualResponseEntity_2.getBody();

        restTemplate.exchange(url + "/" + retFilm1.getId() + "/like/10", HttpMethod.DELETE, null, Film.class);

        ResponseEntity<Film> actualResponseEntity = restTemplate.exchange(url + "/" + retFilm1.getId(), HttpMethod.GET, null, Film.class);

        Film retFilm3 = actualResponseEntity.getBody();

        assertEquals(2, retFilm2.getLikesList().size());
        assertTrue(retFilm2.getLikesList().contains(10L));
        assertTrue(retFilm2.getLikesList().contains(20L));

        assertEquals(1, retFilm3.getLikesList().size());
        assertFalse(retFilm3.getLikesList().contains(10L));
        assertTrue(retFilm3.getLikesList().contains(20L));
    }

    @Test
    @DirtiesContext
    void deleteLikeWithWrongUserIdBehavior() {
        Film testFilm1 = createNewFilm();

        Film retFilm1 = restTemplate.postForObject(url, testFilm1, Film.class);

        restTemplate.exchange(url + "/" + retFilm1.getId() + "/like/10", HttpMethod.PUT, null, Film.class);
        restTemplate.exchange(url + "/" + retFilm1.getId() + "/like/20", HttpMethod.PUT, null, Film.class);
        restTemplate.exchange(url + "/" + retFilm1.getId(), HttpMethod.GET, null, Film.class);

        ResponseEntity<String> actualResponseEntity_2 = restTemplate.exchange(url + "/" + retFilm1.getId() + "/like/100", HttpMethod.DELETE, null, String.class);

        assertEquals(404, actualResponseEntity_2.getStatusCodeValue());
        assertEquals("[\"Ошибка удаления лайка. Фильм с ID = " + retFilm1.getId() + " не содержит лайка от пользователя с id = 100\"]", actualResponseEntity_2.getBody());
    }

    @Test
    @DirtiesContext
    void deleteLikeWithWrongFilmIdBehavior() {
        Film testFilm1 = createNewFilm();

        Film retFilm1 = restTemplate.postForObject(url, testFilm1, Film.class);

        restTemplate.exchange(url + "/" + retFilm1.getId() + "/like/10", HttpMethod.PUT, null, Film.class);
        restTemplate.exchange(url + "/" + retFilm1.getId() + "/like/20", HttpMethod.PUT, null, Film.class);
        restTemplate.exchange(url + "/" + retFilm1.getId(), HttpMethod.GET, null, Film.class);

        ResponseEntity<String> actualResponseEntity_2 = restTemplate.exchange(url + "/" + (retFilm1.getId() + 1000) + "/like/10", HttpMethod.DELETE, null, String.class);

        assertEquals(404, actualResponseEntity_2.getStatusCodeValue());
        assertEquals("[\"Ошибка удаления лайка. Фильм с ID = " + (retFilm1.getId() + 1000) + " не существует\"]", actualResponseEntity_2.getBody());
    }

    @Test
    @DirtiesContext
    void getFilmStandardBehavior() {
        Film testFilm1 = createNewFilm();

        Film retFilm1 = restTemplate.postForObject(url, testFilm1, Film.class);

        ResponseEntity<Film> actualResponseEntity_1 = restTemplate.exchange(url + "/" + retFilm1.getId(), HttpMethod.GET, null, Film.class);

        assertEquals(retFilm1, actualResponseEntity_1.getBody());
    }

    @Test
    @DirtiesContext
    void getFilmWithNegativeIdBehavior() {
        Film testFilm1 = createNewFilm();

        restTemplate.postForObject(url, testFilm1, Film.class);

        ResponseEntity<String> actualResponseEntity_1 = restTemplate.exchange(url + "/-2", HttpMethod.GET, null, String.class);

        assertEquals(400, actualResponseEntity_1.getStatusCodeValue());
        assertEquals("[\"Параметр 'id' не может быть отрицательным\"]", actualResponseEntity_1.getBody());
    }

    @Test
    @DirtiesContext
    void getFilmWithWrongIdBehavior() {
        Film testFilm1 = createNewFilm();

        Film retFilm1 = restTemplate.postForObject(url, testFilm1, Film.class);

        ResponseEntity<String> actualResponseEntity_1 = restTemplate.exchange(url + "/" + (retFilm1.getId() * 1000), HttpMethod.GET, null, String.class);

        assertEquals(404, actualResponseEntity_1.getStatusCodeValue());
        assertEquals("[\"Объект с id = " + (retFilm1.getId() * 1000) + " не найден\"]", actualResponseEntity_1.getBody());
    }

    @Test
    @DirtiesContext
    void getLikedFilmsListStandardBehavior() {
        List<Film> filmList = new ArrayList<>();

        for(int i = 0; i < 12; i++) {
            filmList.add(createNewFilm());
        }

        List<Film> retFilmsList = new ArrayList<>();

        for (Film film : filmList) {
            Film retFilm = restTemplate.postForObject(url, film, Film.class);
            retFilmsList.add(retFilm);
            assertEquals(film, retFilm);
        }

        List<Film> retFilmsList_1 = new ArrayList<>();
        ResponseEntity<Film> actualResponseEntity = null;

        for (int idx_0 = 0; idx_0 < filmList.size(); idx_0++) {
            Film reqFilm = retFilmsList.get(idx_0);

            for (int idx_1 = 0; idx_1 < idx_0; idx_1++) {
                actualResponseEntity = restTemplate.exchange(url + "/" + reqFilm.getId() + "/like/" + idx_1, HttpMethod.PUT, null, Film.class);
            }

            if (actualResponseEntity == null) {
                retFilmsList_1.add(reqFilm);
            } else {
                retFilmsList_1.add(actualResponseEntity.getBody());
            }
        }

        GetFilmsListResponse actualResponse = getFilmsList(url + "/popular?count=5");

        assertEquals(200, actualResponse.statusCode);
        assertEquals(5, actualResponse.filmsList.size());

        for (int idx = 0; idx < 5; idx++) {
            Film exp = retFilmsList_1.get(retFilmsList_1.size() - idx - 1);
            Film act = actualResponse.filmsList.get(idx);
            assertEquals(exp, act);
        }
    }

    @Test
    @DirtiesContext
    void getLikedEmptyQueryBehavior() {
        List<Film> filmList = new ArrayList<>();

        for(int i = 0; i < 12; i++) {
            filmList.add(createNewFilm());
        }

        List<Film> retFilmsList = new ArrayList<>();

        for (Film film : filmList) {
            Film retFilm = restTemplate.postForObject(url, film, Film.class);
            retFilmsList.add(retFilm);
            assertEquals(film, retFilm);
        }

        List<Film> retFilmsList_1 = new ArrayList<>();
        ResponseEntity<Film> actualResponseEntity = null;

        for (int idx_0 = 0; idx_0 < filmList.size(); idx_0++) {
            Film reqFilm = retFilmsList.get(idx_0);

            for (int idx_1 = 0; idx_1 < idx_0; idx_1++) {
                actualResponseEntity = restTemplate.exchange(url + "/" + reqFilm.getId() + "/like/" + idx_1, HttpMethod.PUT, null, Film.class);
            }

            if (actualResponseEntity == null) {
                retFilmsList_1.add(reqFilm);
            } else {
                retFilmsList_1.add(actualResponseEntity.getBody());
            }
        }

        GetFilmsListResponse actualResponse = getFilmsList(url + "/popular?count=");

        assertEquals(200, actualResponse.statusCode);
        assertEquals(10, actualResponse.filmsList.size());

        for (int idx = 0; idx < 10; idx++) {
            Film exp = retFilmsList_1.get(retFilmsList_1.size() - idx - 1);
            Film act = actualResponse.filmsList.get(idx);
            assertEquals(exp, act);
        }
    }

    @Test
    @DirtiesContext
    void getLikedCheckMaxLengthBehavior() {
        List<Film> filmList = new ArrayList<>();

        for(int i = 0; i < 12; i++) {
            filmList.add(createNewFilm());
        }

        List<Film> retFilmsList = new ArrayList<>();

        for (Film film : filmList) {
            Film retFilm = restTemplate.postForObject(url, film, Film.class);
            retFilmsList.add(retFilm);
            assertEquals(film, retFilm);
        }

        List<Film> retFilmsList_1 = new ArrayList<>();
        ResponseEntity<Film> actualResponseEntity = null;

        for (int idx_0 = 0; idx_0 < filmList.size(); idx_0++) {
            Film reqFilm = retFilmsList.get(idx_0);

            for (int idx_1 = 0; idx_1 < idx_0; idx_1++) {
                actualResponseEntity = restTemplate.exchange(url + "/" + reqFilm.getId() + "/like/" + idx_1, HttpMethod.PUT, null, Film.class);
            }

            if (actualResponseEntity == null) {
                retFilmsList_1.add(reqFilm);
            } else {
                retFilmsList_1.add(actualResponseEntity.getBody());
            }
        }

        GetFilmsListResponse actualResponse = getFilmsList(url + "/popular?count=20");

        assertEquals(200, actualResponse.statusCode);
        assertEquals(retFilmsList_1.size(), actualResponse.filmsList.size());

        for (int idx = 0; idx < retFilmsList_1.size(); idx++) {
            Film exp = retFilmsList_1.get(retFilmsList_1.size() - idx - 1);
            Film act = actualResponse.filmsList.get(idx);
            assertEquals(exp, act);
        }
    }


    @Test
    @DirtiesContext
    void updateFilmWithNullLikeListBehavior() {
        Film testFilm1 = createNewFilm();

        Film retFilm_1 = restTemplate.postForObject(url, testFilm1, Film.class);

        Film testFilm2 = new Film(retFilm_1.getId(), "test_film_name_7", "test description 7", LocalDate.parse("1947-01-01", DateTimeFormatter.ISO_DATE), 120, null);

        restTemplate.put(url, testFilm2, Film.class);

        ResponseEntity<Film> actualResponse = restTemplate.exchange(url + "/" + testFilm2.getId(), HttpMethod.GET, null, Film.class);

        assertEquals(200, actualResponse.getStatusCodeValue());

        assertNotNull(actualResponse.getBody().getLikesList());
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

    private Film createNewFilm() {
        return new Film(null, "test_film_name_" + testNumber, "test description " + (testNumber++), LocalDate.parse("1941-01-01", DateTimeFormatter.ISO_DATE), 120, new HashSet<>());
    }
}