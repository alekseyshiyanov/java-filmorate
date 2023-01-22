package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String url;

    @BeforeEach
    void setUp() {
        url = "http://localhost:" + port + "/users";
    }

    @Test
    void getUsersListStandardBehavior() {
        User testUser_1 = new User(null, "tu1@mail.ru","test_user_1", "Test User 1 Name", LocalDate.parse("1941-01-01", DateTimeFormatter.ISO_DATE));
        User testUser_2 = new User(null, "tu2@mail.ru","test_user_2", "Test User 2 Name", LocalDate.parse("1942-01-01", DateTimeFormatter.ISO_DATE));

        User retUser_1 = restTemplate.postForObject(url, testUser_1, User.class);
        User retUser_2 = restTemplate.postForObject(url, testUser_2, User.class);

        GetUsersListResponse actualResponse = getUsersList(url);

        assertEquals(200, actualResponse.statusCode);

        assertEquals(2, actualResponse.usersList.size());
        assertTrue(actualResponse.usersList.contains(retUser_1));
        assertTrue(actualResponse.usersList.contains(retUser_2));
    }

    @Test
    void createUserStandardBehavior() {
        User testUser_3 = new User(null, "tu3@mail.ru","test_user_3", "Test User 3 Name", LocalDate.parse("1943-01-01", DateTimeFormatter.ISO_DATE));
        User testUser_4 = new User(null, "tu4@mail.ru","test_user_4", "Test User 4 Name", LocalDate.parse("1944-01-01", DateTimeFormatter.ISO_DATE));

        User retUser_3 = restTemplate.postForObject(url, testUser_3, User.class);
        assertEquals(testUser_3, retUser_3);

        User retUser_4 = restTemplate.postForObject(url, testUser_4, User.class);
        assertEquals(testUser_4, retUser_4);

        GetUsersListResponse actualResponse = getUsersList(url);

        assertEquals(200, actualResponse.statusCode);

        assertFalse(actualResponse.usersList.isEmpty());
        assertTrue(actualResponse.usersList.contains(testUser_3));
        assertTrue(actualResponse.usersList.contains(testUser_4));
    }

    @Test
    void updateUserStandardBehavior() {
        User testUser_5 = new User(null, "tu5@mail.ru","test_user_5", "Test User 5 Name", LocalDate.parse("1945-01-01", DateTimeFormatter.ISO_DATE));
        User testUser_6 = new User(null, "tu6@mail.ru","test_user_6", "Test User 6 Name", LocalDate.parse("1946-01-01", DateTimeFormatter.ISO_DATE));

        User retUser_5 = restTemplate.postForObject(url, testUser_5, User.class);
        User retUser_6 = restTemplate.postForObject(url, testUser_6, User.class);

        User testUser_7 = new User(retUser_5.getId(), "tu1@mail.ru","test_user_7", "Test User 7 Name", LocalDate.parse("1947-01-01", DateTimeFormatter.ISO_DATE));

        restTemplate.put(url, testUser_7, User.class);

        GetUsersListResponse actualResponse = getUsersList(url);

        assertEquals(200, actualResponse.statusCode);

        assertFalse(actualResponse.usersList.isEmpty());
        assertFalse(actualResponse.usersList.contains(retUser_5));
        assertTrue(actualResponse.usersList.contains(retUser_6));
        assertTrue(actualResponse.usersList.contains(testUser_7));
    }

    @Test
    void updateUserWithWrongUid() {
        User testUser_8 = new User(null, "tu8@mail.ru","test_user_8", "Test User 8 Name", LocalDate.parse("1948-01-01", DateTimeFormatter.ISO_DATE));
        User testUser_9 = new User(null, "tu9@mail.ru","test_user_9", "Test User 9 Name", LocalDate.parse("1949-01-01", DateTimeFormatter.ISO_DATE));

        User retUser_8 = restTemplate.postForObject(url, testUser_8, User.class);
        User retUser_9 = restTemplate.postForObject(url, testUser_9, User.class);

        User testUser_10 = new User(100L, "tu8@mail.ru","test_user_10", "Test User 10 Name", LocalDate.parse("1950-01-01", DateTimeFormatter.ISO_DATE));

        HttpEntity<User> request = new HttpEntity<>(testUser_10);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

        assertEquals(404, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\n\t\"Ошибка обновления объекта. Объект с ID = 100 не существует\"\n]", actualResponseEntity.getBody());
    }

    @Test
    void updateUserWithNullUid() {
        User testUser_11 = new User(null, "tu11@mail.ru","test_user_11", "Test User 11 Name", LocalDate.parse("1951-01-01", DateTimeFormatter.ISO_DATE));
        User testUser_12 = new User(null, "tu12@mail.ru","test_user_12", "Test User 12 Name", LocalDate.parse("1952-01-01", DateTimeFormatter.ISO_DATE));

        User retUser_11 = restTemplate.postForObject(url, testUser_11, User.class);
        User retUser_12 = restTemplate.postForObject(url, testUser_12, User.class);

        User testUser_13 = new User(null, "tu11@mail.ru","test_user_13", "Test User 13 Name", LocalDate.parse("1953-01-01", DateTimeFormatter.ISO_DATE));

        HttpEntity<User> request = new HttpEntity<>(testUser_13);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\n\t\"Ошибка обновления объекта. ID не должен быть null\"\n]", actualResponseEntity.getBody());
    }

    @Test
    void updateUserWithNegativeUid() {
        User testUser_14 = new User(null, "tu14@mail.ru","test_user_14", "Test User 14 Name", LocalDate.parse("1954-01-01", DateTimeFormatter.ISO_DATE));
        User testUser_15 = new User(null, "tu15@mail.ru","test_user_15", "Test User 15 Name", LocalDate.parse("1955-01-01", DateTimeFormatter.ISO_DATE));

        User retUser_14 = restTemplate.postForObject(url, testUser_14, User.class);
        User retUser_15 = restTemplate.postForObject(url, testUser_15, User.class);

        User testUser_16 = new User(-1L, "tu14@mail.ru","test_user_16", "Test User 16 Name", LocalDate.parse("1956-01-01", DateTimeFormatter.ISO_DATE));

        HttpEntity<User> request = new HttpEntity<>(testUser_16);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\n\t\"Ошибка обновления объекта. ID должен быть положительным числом\"\n]", actualResponseEntity.getBody());
    }

    @Test
    void createUserWithEmptyName() {
        User testUser_17 = new User(null, "tu17@mail.ru","test_user_17", "", LocalDate.parse("1957-01-01", DateTimeFormatter.ISO_DATE));

        User retUser_17 = restTemplate.postForObject(url, testUser_17, User.class);
        assertEquals(testUser_17.getLogin(), retUser_17.getName());
    }

    @Test
    void createUserWithEmptyLogin() {
        User testUser_18 = new User(null, "tu18@mail.ru","", "Test User 18 Name", LocalDate.parse("1958-01-01", DateTimeFormatter.ISO_DATE));

        HttpEntity<User> request = new HttpEntity<>(testUser_18);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Проверьте правильность ввода логина: не должен содержать пробелы или быть пустым\"]", actualResponseEntity.getBody());
    }

    @Test
    void createUserWithSpaceInLogin() {
        User testUser_19 = new User(null, "tu19@mail.ru","test user_19", "Test User 19 Name", LocalDate.parse("1959-01-01", DateTimeFormatter.ISO_DATE));

        HttpEntity<User> request = new HttpEntity<>(testUser_19);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Проверьте правильность ввода логина: не должен содержать пробелы или быть пустым\"]", actualResponseEntity.getBody());
    }

    @Test
    void createUserWithNullLogin() {
        User testUser_20 = new User(null, "tu20@mail.ru",null, "Test User 20 Name", LocalDate.parse("1960-01-01", DateTimeFormatter.ISO_DATE));

        HttpEntity<User> request = new HttpEntity<>(testUser_20);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Проверьте правильность ввода логина: не может быть null\"]", actualResponseEntity.getBody());
    }

    @Test
    void createUserWithNullEmail() {
        User testUser_21 = new User(null, null,"test_user_21", "Test User 21 Name", LocalDate.parse("1961-01-01", DateTimeFormatter.ISO_DATE));

        HttpEntity<User> request = new HttpEntity<>(testUser_21);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Проверьте правильность ввода адреса электронной почты: не может быть null\"]", actualResponseEntity.getBody());
    }

    @Test
    void createUserWithWrongEmail() {
        User testUser_22 = new User(null, "tu22&mail.ru","test_user_22", "Test User 22 Name", LocalDate.parse("1962-01-01", DateTimeFormatter.ISO_DATE));

        HttpEntity<User> request = new HttpEntity<>(testUser_22);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Проверьте правильность ввода адреса электронной почты\"]", actualResponseEntity.getBody());
    }

    @Test
    void createUserWithBirthdayInFuture() {
        User testUser_23 = new User(null, "tu23@mail.ru","test_user_23", "Test User 23 Name", LocalDate.parse("3962-01-01", DateTimeFormatter.ISO_DATE));

        HttpEntity<User> request = new HttpEntity<>(testUser_23);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Дата рождения не может быть в будущем\"]", actualResponseEntity.getBody());
    }

    private GetUsersListResponse getUsersList (String url) {
        GetUsersListResponse result = new GetUsersListResponse();

        ResponseEntity<ArrayList<User>> actualResponseEntity =
                restTemplate.exchange(url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {});

        result.usersList = actualResponseEntity.getBody();
        result.statusCode = actualResponseEntity.getStatusCodeValue();

        return result;
    }

    static class GetUsersListResponse {
        public int statusCode;
        public ArrayList<User> usersList;
    }
}