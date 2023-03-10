package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exceptions.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.FriendsDbStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/test_schema.sql", "/test_data.sql"})
class UserDbStorageTest {
    private final UserDbStorage userStorage;
    private final FriendsDbStorage friendsDbStorage;

    @Test
    public void createUserStandardBehaviorTest() {
        User testUser = User.builder()
                .login("test_login_1")
                .name("test_name_1")
                .email("test_1@mail.com")
                .birthday(LocalDate.parse("1941-01-01", DateTimeFormatter.ISO_DATE))
                .build();

        assertThat(userStorage.createUser(testUser)).extracting(User::getId).isEqualTo(7L);
        assertThat(userStorage.getUsersList()).extracting(List::size).isEqualTo(7);
    }

    @Test
    public void createUserEmptyUserNameBehaviorTest() {
        User testUser = User.builder()
                .login("test_login_2")
                .name("")
                .email("test_2@mail.com")
                .birthday(LocalDate.parse("1941-01-01", DateTimeFormatter.ISO_DATE))
                .build();

        User retUser = userStorage.createUser(testUser);

        assertThat(retUser).extracting(User::getName).isEqualTo("test_login_2");
    }

    @Test
    public void getUserTest() {
        FilmorateNotFoundException exception = assertThrows(FilmorateNotFoundException.class, () -> userStorage.getUser(10L));
        assertEquals("Пользователь с ID = 10 не найден", exception.getMessage());

        assertThat(userStorage.getUser(1L)).extracting(User::getId).isEqualTo(1L);
    }

    @Test
    public void getUsersListTest() {

        List<User> userList = userStorage.getUsersList();
        assertThat(userList).extracting(List::size).isEqualTo(6);
    }

    @Test
    public void updateUserStandardBehaviorTest() {
        User testUser = User.builder()
                .id(1L)
                .login("test_login_3")
                .name("test_name_3")
                .email("test_3@mail.com")
                .birthday(LocalDate.parse("1941-01-01", DateTimeFormatter.ISO_DATE))
                .build();

        User retUser = userStorage.updateUser(testUser);

        assertThat(retUser).extracting(User::getLogin).isEqualTo("test_login_3");
        assertThat(retUser).extracting(User::getEmail).isEqualTo("test_3@mail.com");
    }

    @Test
    public void updateUserWrongUserIdBehaviorTest() {
        User testUser = User.builder()
                .id(1000L)
                .login("test_login_4")
                .name("test_name_4")
                .email("test_4@mail.com")
                .birthday(LocalDate.parse("1941-01-01", DateTimeFormatter.ISO_DATE))
                .build();

        FilmorateNotFoundException exception = assertThrows(FilmorateNotFoundException.class, () -> userStorage.updateUser(testUser));
        assertEquals("Ошибка при обновлении данных пользователя. Пользователь с ID = 1000 не существует", exception.getMessage());
    }

    @Test
    public void addFriendsTest() {
        friendsDbStorage.addFriends(1L, 2L);

        User user = userStorage.getUser(1L);
        Set<Long> fl = user.getFriends();
        assertNotNull(fl);
        assertTrue(fl.contains(2L));
    }

    @Test
    public void deleteFriendsTest() {
        friendsDbStorage.addFriends(1L, 2L);
        friendsDbStorage.addFriends(1L, 3L);
        friendsDbStorage.addFriends(1L, 4L);

        User user_0 = userStorage.getUser(1L);
        Set<Long> fl_0 = user_0.getFriends();
        assertNotNull(fl_0);
        assertEquals(3, fl_0.size());
        assertTrue(fl_0.contains(2L));
        assertTrue(fl_0.contains(3L));
        assertTrue(fl_0.contains(4L));

        friendsDbStorage.deleteFriend(1L, 2L);

        User user_1 = userStorage.getUser(1L);
        Set<Long> fl_1 = user_1.getFriends();
        assertNotNull(fl_1);
        assertEquals(2, fl_1.size());
        assertFalse(fl_1.contains(2L));
        assertTrue(fl_1.contains(3L));
        assertTrue(fl_1.contains(4L));
    }

    @Test
    public void getFriendsListTest() {
        friendsDbStorage.addFriends(1L, 2L);
        friendsDbStorage.addFriends(1L, 3L);
        friendsDbStorage.addFriends(1L, 4L);

        List<User> userFL = userStorage.getFriendsList(1L);
        assertEquals(3, userFL.size());
    }

    @Test
    public void getCommonFriendsListTest() {
        friendsDbStorage.addFriends(1L, 3L);
        friendsDbStorage.addFriends(2L, 3L);
        friendsDbStorage.addFriends(1L, 4L);

        List<User> userCFL = userStorage.getCommonFriendsList(1L, 2L);
        assertEquals(1, userCFL.size());
        assertEquals(3L, userCFL.iterator().next().getId());
    }
}