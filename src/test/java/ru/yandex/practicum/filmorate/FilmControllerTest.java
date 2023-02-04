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
    void createFilmStandardBehavior() {
        Film testFilm3 = createNewFilm();
        Film testFilm4 = createNewFilm();

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
        Film testFilm5 = createNewFilm();
        Film testFilm6 = createNewFilm();

        Film retFilm5 = restTemplate.postForObject(url, testFilm5, Film.class);
        Film retFilm6 = restTemplate.postForObject(url, testFilm6, Film.class);

        Film testFilm7 = new Film(retFilm5.getId(), "test_film_name_7", "test description 7", LocalDate.parse("1947-01-01", DateTimeFormatter.ISO_DATE), 120, new HashSet<>());

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
        Film testFilm8 = createNewFilm();
        Film testFilm9 = createNewFilm();

        restTemplate.postForObject(url, testFilm8, Film.class);
        restTemplate.postForObject(url, testFilm9, Film.class);

        Film testFilm10 = new Film(100L, "test_film_name_10", "test description 10", LocalDate.parse("1950-01-01", DateTimeFormatter.ISO_DATE), 120, new HashSet<>());

        HttpEntity<Film> request = new HttpEntity<>(testFilm10);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

        assertEquals(404, actualResponseEntity.getStatusCodeValue());
        assertEquals("Ошибка обновления объекта. Объект с ID = 100 не существует", actualResponseEntity.getBody());
    }

    @Test
    void updateFilmWithNullUid() {
        Film testFilm11 = createNewFilm();
        Film testFilm12 = createNewFilm();

        restTemplate.postForObject(url, testFilm11, Film.class);
        restTemplate.postForObject(url, testFilm12, Film.class);

        Film testFilm13 = new Film(null, "test_film_name_13", "test description 13", LocalDate.parse("1953-01-01", DateTimeFormatter.ISO_DATE), 120, new HashSet<>());

        HttpEntity<Film> request = new HttpEntity<>(testFilm13);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("Ошибка обновления объекта. ID не должен быть null", actualResponseEntity.getBody());
    }

    @Test
    void updateFilmWithNegativeUid() {
        Film testFilm14 = createNewFilm();
        Film testFilm15 = createNewFilm();

        restTemplate.postForObject(url, testFilm14, Film.class);
        restTemplate.postForObject(url, testFilm15, Film.class);

        Film testFilm16 = new Film(-1L, "test_film_name_16", "test description 16", LocalDate.parse("1956-01-01", DateTimeFormatter.ISO_DATE), 120, new HashSet<>());

        HttpEntity<Film> request = new HttpEntity<>(testFilm16);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("Ошибка обновления объекта. ID должен быть положительным числом", actualResponseEntity.getBody());
    }

    @Test
    void createFilmWithNullReleaseDate() {
        Film testFilm17 = new Film(null, "test_film_name_17", "test description 17", null, 120, new HashSet<>());

        HttpEntity<Film> request = new HttpEntity<>(testFilm17);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("Дата релиза не может быть null", actualResponseEntity.getBody());
    }

    @Test
    void createFilmWithToEarlyReleaseDate() {
        Film testFilm18 = new Film(null, "test_film_name_18", "test description 18", LocalDate.parse("1856-01-01", DateTimeFormatter.ISO_DATE), 120, new HashSet<>());

        HttpEntity<Film> request = new HttpEntity<>(testFilm18);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("Дата релиза не может быть ранее 1895-12-28", actualResponseEntity.getBody());
    }

    @Test
    void createFilmWithEmptyName() {
        Film testFilm19 = new Film(null, "", "test description 19", LocalDate.parse("1956-01-01", DateTimeFormatter.ISO_DATE), 120, new HashSet<>());

        HttpEntity<Film> request = new HttpEntity<>(testFilm19);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Название фильма не может быть пустым\"]", actualResponseEntity.getBody());
    }

    @Test
    void createFilmWithNullName() {
        Film testFilm20 = new Film(null, null, "test description 20", LocalDate.parse("1956-01-01", DateTimeFormatter.ISO_DATE), 120, new HashSet<>());

        HttpEntity<Film> request = new HttpEntity<>(testFilm20);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Название фильма не может быть пустым\"]", actualResponseEntity.getBody());
    }

    @Test
    void createFilmWithNegativeDuration() {
        Film testFilm21 = new Film(null, "test_film_name_21", "test description 21", LocalDate.parse("1956-01-01", DateTimeFormatter.ISO_DATE), -120, new HashSet<>());

        HttpEntity<Film> request = new HttpEntity<>(testFilm21);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Продолжительность фильма должна быть положительной\"]", actualResponseEntity.getBody());
    }

    @Test
    void addLikeStandardBehavior() {
        Film testFilm22 = createNewFilm();

        Film retFilm22 = restTemplate.postForObject(url, testFilm22, Film.class);

        ResponseEntity<Film> actualResponseEntity = restTemplate.exchange(url + "/" + retFilm22.getId() + "/like/111", HttpMethod.PUT, null, Film.class);

        assertEquals(200, actualResponseEntity.getStatusCodeValue());
        assertTrue(actualResponseEntity.getBody().getLikesList().contains(111L));
    }

    @Test
    void addLikeWithWrongFilmUidBehavior() {
        Film testFilm23 = createNewFilm();

        Film retFilm23 = restTemplate.postForObject(url, testFilm23, Film.class);

        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url + "/" + (retFilm23.getId() + 1000L) + "/like/111", HttpMethod.PUT, null, String.class);

        assertEquals(404, actualResponseEntity.getStatusCodeValue());
        assertEquals("Запись типа Film с id = " + (retFilm23.getId() + 1000L) + " не найдена", actualResponseEntity.getBody());
    }

    @Test
    void addLikeWithNullFilmUidBehavior() {
        Film testFilm24 = createNewFilm();

        Film retFilm24 = restTemplate.postForObject(url, testFilm24, Film.class);

        retFilm24.setId(null);

        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url + "/" + retFilm24.getId() + "/like/111", HttpMethod.PUT, null, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("Параметр 'id' должен быть числом", actualResponseEntity.getBody());
    }

    @Test
    void addLikeWithEmptyFilmUidBehavior() {
        Film testFilm25 = createNewFilm();

        restTemplate.postForObject(url, testFilm25, Film.class);

        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url + "/"  + "/like/111", HttpMethod.PUT, null, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("Пропущен обязательный параметр 'id'", actualResponseEntity.getBody());
    }

    @Test
    void addLikeWithNegativeFilmUidBehavior() {
        Film testFilm26 = createNewFilm();

        Film retFilm26 = restTemplate.postForObject(url, testFilm26, Film.class);

        retFilm26.setId(-1L);

        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url + "/" + retFilm26.getId() + "/like/111", HttpMethod.PUT, null, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("Параметр 'id' не может быть отрицательным", actualResponseEntity.getBody());
    }

    @Test
    void addLikeWithNullUserIdBehavior() {
        Film testFilm27 = createNewFilm();

        Film retFilm27 = restTemplate.postForObject(url, testFilm27, Film.class);

        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url + "/" + retFilm27.getId() + "/like/null", HttpMethod.PUT, null, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("Параметр 'userId' должен быть числом", actualResponseEntity.getBody());
    }

    @Test
    void addLikeWithEmptyUserIdBehavior() {
        Film testFilm28 = createNewFilm();

        Film retFilm28 = restTemplate.postForObject(url, testFilm28, Film.class);

        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url + "/" + retFilm28.getId() + "/like/", HttpMethod.PUT, null, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("Пропущен обязательный параметр 'userId'", actualResponseEntity.getBody());
    }

    @Test
    void addLikeWithNegativeUserIdBehavior() {
        Film testFilm29 = createNewFilm();

        Film retFilm29 = restTemplate.postForObject(url, testFilm29, Film.class);

        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url + "/" + retFilm29.getId() + "/like/-1", HttpMethod.PUT, null, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("Параметр 'userId' не может быть отрицательным", actualResponseEntity.getBody());
    }

    @Test
    void deleteLikeStandardIdBehavior() {
        Film testFilm30 = createNewFilm();

        Film retFilm30 = restTemplate.postForObject(url, testFilm30, Film.class);

        restTemplate.exchange(url + "/" + retFilm30.getId() + "/like/10", HttpMethod.PUT, null, Film.class);
        restTemplate.exchange(url + "/" + retFilm30.getId() + "/like/20", HttpMethod.PUT, null, Film.class);

        ResponseEntity<Film> actualResponseEntity_2 = restTemplate.exchange(url + "/" + retFilm30.getId(), HttpMethod.GET, null, Film.class);

        Film retFilm31 = actualResponseEntity_2.getBody();

        restTemplate.exchange(url + "/" + retFilm30.getId() + "/like/10", HttpMethod.DELETE, null, Film.class);

        ResponseEntity<Film> actualResponseEntity = restTemplate.exchange(url + "/" + retFilm30.getId(), HttpMethod.GET, null, Film.class);

        Film retFilm32 = actualResponseEntity.getBody();

        assertEquals(2, retFilm31.getLikesList().size());
        assertTrue(retFilm31.getLikesList().contains(10L));
        assertTrue(retFilm31.getLikesList().contains(20L));

        assertEquals(1, retFilm32.getLikesList().size());
        assertFalse(retFilm32.getLikesList().contains(10L));
        assertTrue(retFilm32.getLikesList().contains(20L));
    }

    @Test
    void deleteLikeWithWrongUserIdBehavior() {
        Film testFilm33 = createNewFilm();

        Film retFilm33 = restTemplate.postForObject(url, testFilm33, Film.class);

        restTemplate.exchange(url + "/" + retFilm33.getId() + "/like/10", HttpMethod.PUT, null, Film.class);
        restTemplate.exchange(url + "/" + retFilm33.getId() + "/like/20", HttpMethod.PUT, null, Film.class);

        ResponseEntity<Film> actualResponseEntity_1 = restTemplate.exchange(url + "/" + retFilm33.getId(), HttpMethod.GET, null, Film.class);

        Film retFilm34 = actualResponseEntity_1.getBody();

        restTemplate.exchange(url + "/" + retFilm33.getId() + "/like/100", HttpMethod.DELETE, null, Film.class);

        ResponseEntity<Film> actualResponseEntity_2 = restTemplate.exchange(url + "/" + retFilm33.getId(), HttpMethod.GET, null, Film.class);

        Film retFilm35 = actualResponseEntity_2.getBody();

        assertEquals(retFilm34, retFilm35);
    }

    @Test
    void deleteLikeWithWrongFilmIdBehavior() {
        Film testFilm36 = createNewFilm();

        Film retFilm36 = restTemplate.postForObject(url, testFilm36, Film.class);

        restTemplate.exchange(url + "/" + retFilm36.getId() + "/like/10", HttpMethod.PUT, null, Film.class);
        restTemplate.exchange(url + "/" + retFilm36.getId() + "/like/20", HttpMethod.PUT, null, Film.class);
        restTemplate.exchange(url + "/" + retFilm36.getId(), HttpMethod.GET, null, Film.class);

        ResponseEntity<String> actualResponseEntity_2 = restTemplate.exchange(url + "/" + (retFilm36.getId() + 1000) + "/like/10", HttpMethod.DELETE, null, String.class);

        assertEquals(404, actualResponseEntity_2.getStatusCodeValue());
        assertEquals("Запись типа Film с id = " + (retFilm36.getId() + 1000) + " не найдена", actualResponseEntity_2.getBody());
    }

    @Test
    void getFilmStandardBehavior() {
        Film testFilm39 = createNewFilm();

        Film retFilm39 = restTemplate.postForObject(url, testFilm39, Film.class);

        ResponseEntity<Film> actualResponseEntity_1 = restTemplate.exchange(url + "/" + retFilm39.getId(), HttpMethod.GET, null, Film.class);

        assertEquals(retFilm39, actualResponseEntity_1.getBody());
    }

    @Test
    void getFilmWithNegativeIdBehavior() {
        Film testFilm42 = createNewFilm();

        restTemplate.postForObject(url, testFilm42, Film.class);

        ResponseEntity<String> actualResponseEntity_1 = restTemplate.exchange(url + "/-2", HttpMethod.GET, null, String.class);

        assertEquals(400, actualResponseEntity_1.getStatusCodeValue());
        assertEquals("Параметр 'id' не может быть отрицательным", actualResponseEntity_1.getBody());
    }

    @Test
    void getFilmWithWrongIdBehavior() {
        Film testFilm44= createNewFilm();

        Film retFilm44 = restTemplate.postForObject(url, testFilm44, Film.class);

        ResponseEntity<String> actualResponseEntity_1 = restTemplate.exchange(url + "/" + (retFilm44.getId() * 1000), HttpMethod.GET, null, String.class);

        assertEquals(404, actualResponseEntity_1.getStatusCodeValue());
        assertEquals("Объект с id = " + (retFilm44.getId() * 1000) + " не найден", actualResponseEntity_1.getBody());
    }

    @Test
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