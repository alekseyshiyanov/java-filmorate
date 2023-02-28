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
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String url;

    private int testNumber = 1;

    @BeforeEach
    void setUp() {
        url = "http://localhost:" + port + "/users";
    }

    @Test
    @DirtiesContext
    void getUsersListStandardBehavior() {
        User testUser_1 = createNewUser();
        User testUser_2 = createNewUser();

        User retUser_1 = restTemplate.postForObject(url, testUser_1, User.class);
        User retUser_2 = restTemplate.postForObject(url, testUser_2, User.class);

        GetUsersListResponse actualResponse = getUsersList(url);

        assertEquals(200, actualResponse.statusCode);

        assertEquals(2, actualResponse.usersList.size());
        assertTrue(actualResponse.usersList.contains(retUser_1));
        assertTrue(actualResponse.usersList.contains(retUser_2));
    }

    @Test
    @DirtiesContext
    void getCommonFriendsListStandardBehavior() {
        User testUser_1 = createNewUser();
        User testUser_2 = createNewUser();
        User testUser_3 = createNewUser();
        User testUser_4 = createNewUser();

        User retUser_1 = restTemplate.postForObject(url, testUser_1, User.class);
        User retUser_2 = restTemplate.postForObject(url, testUser_2, User.class);
        User retUser_3 = restTemplate.postForObject(url, testUser_3, User.class);
        User retUser_4 = restTemplate.postForObject(url, testUser_4, User.class);

        restTemplate.put(url + "/" + retUser_1.getId() + "/friends/" + retUser_2.getId(), testUser_1, User.class);
        restTemplate.put(url + "/" + retUser_1.getId() + "/friends/" + retUser_3.getId(), testUser_1, User.class);
        restTemplate.put(url + "/" + retUser_1.getId() + "/friends/" + retUser_4.getId(), testUser_1, User.class);
        restTemplate.put(url + "/" + retUser_3.getId() + "/friends/" + retUser_1.getId(), testUser_1, User.class);
        restTemplate.put(url + "/" + retUser_3.getId() + "/friends/" + retUser_4.getId(), testUser_1, User.class);
        restTemplate.put(url + "/" + retUser_3.getId() + "/friends/" + retUser_2.getId(), testUser_1, User.class);

        ResponseEntity<ArrayList<User>> actualResponse_1 = restTemplate.exchange(url + "/" + retUser_1.getId() + "/friends/common/" + retUser_3.getId(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        assertEquals(200, actualResponse_1.getStatusCodeValue());

        ResponseEntity<User> actualResponse_2 = restTemplate.exchange(url + "/" + retUser_2.getId(), HttpMethod.GET, null, User.class);
        assertEquals(200, actualResponse_2.getStatusCodeValue());

        ResponseEntity<User> actualResponse_3 = restTemplate.exchange(url + "/" + retUser_4.getId(), HttpMethod.GET, null, User.class);
        assertEquals(200, actualResponse_3.getStatusCodeValue());


        assertEquals(2, actualResponse_1.getBody().size());
        assertTrue(actualResponse_1.getBody().contains(actualResponse_2.getBody()));
        assertTrue(actualResponse_1.getBody().contains(actualResponse_3.getBody()));
    }

    @Test
    @DirtiesContext
    void createUserStandardBehavior() {
        User testUser_1 = createNewUser();
        User testUser_2 = createNewUser();

        User retUser_1 = restTemplate.postForObject(url, testUser_1, User.class);
        User retUser_2 = restTemplate.postForObject(url, testUser_2, User.class);

        assertEquals(testUser_1, retUser_1);
        assertEquals(testUser_2, retUser_2);

        GetUsersListResponse actualResponse = getUsersList(url);

        assertEquals(200, actualResponse.statusCode);

        assertFalse(actualResponse.usersList.isEmpty());
        assertTrue(actualResponse.usersList.contains(testUser_1));
        assertTrue(actualResponse.usersList.contains(testUser_2));
    }

    @Test
    @DirtiesContext
    void createUserWithNullFriendsListBehavior() {
        User testUser_1 = new User(null, "tu7@mail.ru","test_user_7", "Test User 7 Name", LocalDate.parse("1947-01-01", DateTimeFormatter.ISO_DATE), null);

        User retUser_1 = restTemplate.postForObject(url, testUser_1, User.class);

        ResponseEntity<User> actualResponse = restTemplate.exchange(url + "/" + retUser_1.getId(), HttpMethod.GET, null, User.class);

        assertEquals(200, actualResponse.getStatusCodeValue());

        assertNotNull(actualResponse.getBody().getFriends());
    }

    @Test
    @DirtiesContext
    void getUserStandardBehavior() {
        User testUser_1 = createNewUser();
        User testUser_2 = createNewUser();

        restTemplate.postForObject(url, testUser_1, User.class);
        User retUser_1 = restTemplate.postForObject(url, testUser_2, User.class);

        ResponseEntity<User> actualResponse = restTemplate.exchange(url + "/" + retUser_1.getId(), HttpMethod.GET, null, User.class);

        assertEquals(200, actualResponse.getStatusCodeValue());

        assertEquals(retUser_1, actualResponse.getBody());
    }

    @Test
    @DirtiesContext
    void getUserWithNegativeIdBehavior() {
        User testUser_1 = createNewUser();
        User testUser_2 = createNewUser();

        restTemplate.postForObject(url, testUser_1, User.class);
        restTemplate.postForObject(url, testUser_2, User.class);

        ResponseEntity<String> actualResponse = restTemplate.exchange(url + "/-1", HttpMethod.GET, null, String.class);

        assertEquals(404, actualResponse.getStatusCodeValue());

        assertEquals("[\"Пользователь с userId = -1 не существует\"]", actualResponse.getBody());
    }

    @Test
    @DirtiesContext
    void getUserWithWrongIdBehavior() {
        User testUser_1 = createNewUser();
        User testUser_2 = createNewUser();

        restTemplate.postForObject(url, testUser_1, User.class);
        restTemplate.postForObject(url, testUser_2, User.class);

        ResponseEntity<String> actualResponse = restTemplate.exchange(url + "/1000", HttpMethod.GET, null, String.class);

        assertEquals(404, actualResponse.getStatusCodeValue());

        assertEquals("[\"Пользователь с id = 1000 не найден\"]", actualResponse.getBody());
    }

    @Test
    @DirtiesContext
    void addFriendStandardBehavior() {
        User testUser_1 = createNewUser();
        User testUser_2 = createNewUser();

        User retUser_1 = restTemplate.postForObject(url, testUser_1, User.class);
        User retUser_2 = restTemplate.postForObject(url, testUser_2, User.class);

        restTemplate.put(url + "/" + retUser_1.getId() + "/friends/" + retUser_2.getId(), testUser_1, User.class);

        ResponseEntity<User> actualResponse_1 = restTemplate.exchange(url + "/" + retUser_1.getId(), HttpMethod.GET, null, User.class);
        assertEquals(200, actualResponse_1.getStatusCodeValue());

        ResponseEntity<User> actualResponse_2 = restTemplate.exchange(url + "/" + retUser_2.getId(), HttpMethod.GET, null, User.class);
        assertEquals(200, actualResponse_2.getStatusCodeValue());

        assertTrue(actualResponse_1.getBody().getFriends().contains(retUser_2.getId()));
        assertTrue(actualResponse_2.getBody().getFriends().contains(retUser_1.getId()));
    }

    @Test
    @DirtiesContext
    void deleteFriendStandardBehavior() {
        User testUser_1 = createNewUser();
        User testUser_2 = createNewUser();

        User retUser_1 = restTemplate.postForObject(url, testUser_1, User.class);
        User retUser_2 = restTemplate.postForObject(url, testUser_2, User.class);

        restTemplate.put(url + "/" + retUser_1.getId() + "/friends/" + retUser_2.getId(), testUser_1, User.class);

        ResponseEntity<User> actualResponse_1 = restTemplate.exchange(url + "/" + retUser_1.getId(), HttpMethod.GET, null, User.class);
        assertEquals(200, actualResponse_1.getStatusCodeValue());

        ResponseEntity<User> actualResponse_2 = restTemplate.exchange(url + "/" + retUser_2.getId(), HttpMethod.GET, null, User.class);
        assertEquals(200, actualResponse_2.getStatusCodeValue());

        restTemplate.exchange(url + "/" + retUser_1.getId() + "/friends/" + retUser_2.getId(), HttpMethod.DELETE, null, String.class);

        ResponseEntity<User> actualResponse_4 = restTemplate.exchange(url + "/" + retUser_1.getId(), HttpMethod.GET, null, User.class);
        assertEquals(200, actualResponse_4.getStatusCodeValue());

        ResponseEntity<User> actualResponse_5 = restTemplate.exchange(url + "/" + retUser_2.getId(), HttpMethod.GET, null, User.class);
        assertEquals(200, actualResponse_5.getStatusCodeValue());

        assertTrue(actualResponse_1.getBody().getFriends().contains(retUser_2.getId()));
        assertTrue(actualResponse_2.getBody().getFriends().contains(retUser_1.getId()));
        assertFalse(actualResponse_4.getBody().getFriends().contains(retUser_2.getId()));
        assertFalse(actualResponse_5.getBody().getFriends().contains(retUser_1.getId()));
    }

    @Test
    @DirtiesContext
    void deleteFriendWrongId_1_Behavior() {
        User testUser_1 = createNewUser();
        User testUser_2 = createNewUser();

        User retUser_1 = restTemplate.postForObject(url, testUser_1, User.class);
        User retUser_2 = restTemplate.postForObject(url, testUser_2, User.class);

        ResponseEntity<String> actualResponse_1 = restTemplate.exchange(url + "/" + retUser_1.getId() + "/friends/" + retUser_2.getId(), HttpMethod.DELETE, null, String.class);

        assertEquals(404, actualResponse_1.getStatusCodeValue());
        assertEquals("[\"Пользователь с ID = " + retUser_1.getId() + " не связан пользователем с ID = " + retUser_2.getId() + "\"]", actualResponse_1.getBody());
    }

    @Test
    @DirtiesContext
    void deleteFriendWrongId_2_Behavior() {
        User testUser_1 = createNewUser();
        User testUser_2 = createNewUser();

        User retUser_1 = restTemplate.postForObject(url, testUser_1, User.class);
        User retUser_2 = restTemplate.postForObject(url, testUser_2, User.class);

        restTemplate.put(url + "/" + retUser_1.getId() + "/friends/" + retUser_2.getId(), null, User.class);

        restTemplate.put(url, retUser_2, User.class);

        ResponseEntity<String> actualResponse_1 = restTemplate.exchange(url + "/" + retUser_1.getId() + "/friends/" + retUser_2.getId(), HttpMethod.DELETE, null, String.class);

        assertEquals(404, actualResponse_1.getStatusCodeValue());
        assertEquals("[\"Пользователь с ID = " + retUser_2.getId() + " не связан пользователем с ID = " + retUser_1.getId() + "\"]", actualResponse_1.getBody());
    }

    @Test
    @DirtiesContext
    void deleteFriendWrongId_3_Behavior() {
        User testUser_1 = createNewUser();
        User testUser_2 = createNewUser();

        User retUser_1 = restTemplate.postForObject(url, testUser_1, User.class);
        User retUser_2 = restTemplate.postForObject(url, testUser_2, User.class);

        restTemplate.put(url + "/" + retUser_1.getId() + "/friends/" + retUser_2.getId(), null, User.class);

        ResponseEntity<String> actualResponse_1 = restTemplate.exchange(url + "/" + (retUser_1.getId() + 100) + "/friends/" + retUser_2.getId(), HttpMethod.DELETE, null, String.class);

        assertEquals(404, actualResponse_1.getStatusCodeValue());
        assertEquals("[\"Пользователь с ID = " + (retUser_1.getId() + 100) + " не существует\"]", actualResponse_1.getBody());
    }

    @Test
    @DirtiesContext
    void getFriendsListStandardBehavior() {
        User testUser_1 = createNewUser();
        User testUser_2 = createNewUser();
        User testUser_3 = createNewUser();

        User retUser_1 = restTemplate.postForObject(url, testUser_1, User.class);
        User retUser_2 = restTemplate.postForObject(url, testUser_2, User.class);
        User retUser_3 = restTemplate.postForObject(url, testUser_3, User.class);

        restTemplate.put(url + "/" + retUser_1.getId() + "/friends/" + retUser_2.getId(), testUser_1, User.class);
        restTemplate.put(url + "/" + retUser_1.getId() + "/friends/" + retUser_3.getId(), testUser_1, User.class);

        ResponseEntity<ArrayList<User>> actualResponse_1 = restTemplate.exchange(url + "/" + retUser_1.getId() + "/friends", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        assertEquals(200, actualResponse_1.getStatusCodeValue());

        ResponseEntity<User> actualResponse_2 = restTemplate.exchange(url + "/" + retUser_2.getId(), HttpMethod.GET, null, User.class);
        assertEquals(200, actualResponse_2.getStatusCodeValue());

        ResponseEntity<User> actualResponse_3 = restTemplate.exchange(url + "/" + retUser_3.getId(), HttpMethod.GET, null, User.class);
        assertEquals(200, actualResponse_3.getStatusCodeValue());

        assertTrue(actualResponse_1.getBody().contains(actualResponse_2.getBody()));
        assertTrue(actualResponse_1.getBody().contains(actualResponse_3.getBody()));
    }

    @Test
    @DirtiesContext
    void updateUserStandardBehavior() {
        User testUser_1 = createNewUser();
        User testUser_2 = createNewUser();

        User retUser_1 = restTemplate.postForObject(url, testUser_1, User.class);
        User retUser_2 = restTemplate.postForObject(url, testUser_2, User.class);

        User testUser_3 = new User(retUser_1.getId(), "tu7@mail.ru","test_user_7", "Test User 7 Name", LocalDate.parse("1947-01-01", DateTimeFormatter.ISO_DATE), new HashSet<>());

        restTemplate.put(url, testUser_3, User.class);

        GetUsersListResponse actualResponse = getUsersList(url);

        assertEquals(200, actualResponse.statusCode);

        assertFalse(actualResponse.usersList.isEmpty());
        assertFalse(actualResponse.usersList.contains(retUser_1));
        assertTrue(actualResponse.usersList.contains(retUser_2));
        assertTrue(actualResponse.usersList.contains(testUser_3));
    }

    @Test
    @DirtiesContext
    void updateUserWithWrongUid() {
        User testUser_1 = createNewUser();
        User testUser_2 = createNewUser();

        restTemplate.postForObject(url, testUser_1, User.class);
        restTemplate.postForObject(url, testUser_2, User.class);

        User testUser_3 = new User(100L, "tu8@mail.ru","test_user_10", "Test User 10 Name", LocalDate.parse("1950-01-01", DateTimeFormatter.ISO_DATE), new HashSet<>());

        HttpEntity<User> request = new HttpEntity<>(testUser_3);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

        assertEquals(404, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Ошибка обновления объекта. Объект с ID = 100 не существует\"]", actualResponseEntity.getBody());
    }

    @Test
    @DirtiesContext
    void updateUserWithNullUid() {
        User testUser_1 = createNewUser();
        User testUser_2 = createNewUser();

        restTemplate.postForObject(url, testUser_1, User.class);
        restTemplate.postForObject(url, testUser_2, User.class);

        User testUser_3 = new User(null, "tu11@mail.ru","test_user_13", "Test User 13 Name", LocalDate.parse("1953-01-01", DateTimeFormatter.ISO_DATE), new HashSet<>());

        HttpEntity<User> request = new HttpEntity<>(testUser_3);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Ошибка обновления объекта. ID не должен быть null\"]", actualResponseEntity.getBody());
    }

    @Test
    @DirtiesContext
    void updateUserWithNegativeUid() {
        User testUser_1 = createNewUser();
        User testUser_2 = createNewUser();

        restTemplate.postForObject(url, testUser_1, User.class);
        restTemplate.postForObject(url, testUser_2, User.class);

        User testUser_3 = new User(-1L, "tu14@mail.ru","test_user_16", "Test User 16 Name", LocalDate.parse("1956-01-01", DateTimeFormatter.ISO_DATE), new HashSet<>());

        HttpEntity<User> request = new HttpEntity<>(testUser_3);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Ошибка обновления объекта. ID должен быть положительным числом\"]", actualResponseEntity.getBody());
    }

    @Test
    @DirtiesContext
    void createUserWithEmptyName() {
        User testUser_1 = new User(null, "tu17@mail.ru","test_user_17", "", LocalDate.parse("1957-01-01", DateTimeFormatter.ISO_DATE), new HashSet<>());

        User retUser_1 = restTemplate.postForObject(url, testUser_1, User.class);
        assertEquals(testUser_1.getLogin(), retUser_1.getName());
    }

    @Test
    @DirtiesContext
    void createUserWithEmptyLogin() {
        User testUser_1 = new User(null, "tu18@mail.ru","", "Test User 18 Name", LocalDate.parse("1958-01-01", DateTimeFormatter.ISO_DATE), new HashSet<>());

        HttpEntity<User> request = new HttpEntity<>(testUser_1);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Проверьте правильность ввода логина: не должен содержать пробелы или быть пустым\"]", actualResponseEntity.getBody());
    }

    @Test
    @DirtiesContext
    void createUserWithSpaceInLogin() {
        User testUser_1 = new User(null, "tu19@mail.ru","test user_19", "Test User 19 Name", LocalDate.parse("1959-01-01", DateTimeFormatter.ISO_DATE), new HashSet<>());

        HttpEntity<User> request = new HttpEntity<>(testUser_1);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Проверьте правильность ввода логина: не должен содержать пробелы или быть пустым\"]", actualResponseEntity.getBody());
    }

    @Test
    @DirtiesContext
    void createUserWithNullLogin() {
        User testUser_1 = new User(null, "tu20@mail.ru",null, "Test User 20 Name", LocalDate.parse("1960-01-01", DateTimeFormatter.ISO_DATE), new HashSet<>());

        HttpEntity<User> request = new HttpEntity<>(testUser_1);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Проверьте правильность ввода логина: не может быть null\"]", actualResponseEntity.getBody());
    }

    @Test
    @DirtiesContext
    void createUserWithNullEmail() {
        User testUser_1 = new User(null, null,"test_user_21", "Test User 21 Name", LocalDate.parse("1961-01-01", DateTimeFormatter.ISO_DATE), new HashSet<>());

        HttpEntity<User> request = new HttpEntity<>(testUser_1);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Проверьте правильность ввода адреса электронной почты: не может быть null\"]", actualResponseEntity.getBody());
    }

    @Test
    @DirtiesContext
    void createUserWithWrongEmail() {
        User testUser_1 = new User(null, "tu22&mail.ru","test_user_22", "Test User 22 Name", LocalDate.parse("1962-01-01", DateTimeFormatter.ISO_DATE), new HashSet<>());

        HttpEntity<User> request = new HttpEntity<>(testUser_1);
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());
        assertEquals("[\"Проверьте правильность ввода адреса электронной почты\"]", actualResponseEntity.getBody());
    }

    @Test
    @DirtiesContext
    void createUserWithBirthdayInFuture() {
        User testUser_1 = new User(null, "tu23@mail.ru","test_user_23", "Test User 23 Name", LocalDate.parse("3962-01-01", DateTimeFormatter.ISO_DATE), new HashSet<>());

        HttpEntity<User> request = new HttpEntity<>(testUser_1);
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
    private User createNewUser() {
        String email = "tu" + testNumber + "@mail.ru";
        String login = "test_user_" + testNumber;
        String name = "Test User " + testNumber + " Name";

        testNumber++;

        return new User(null, email,login, name, LocalDate.parse("1941-01-01", DateTimeFormatter.ISO_DATE), new HashSet<>());
    }

    static class GetUsersListResponse {
        public int statusCode;
        public ArrayList<User> usersList;
    }
}