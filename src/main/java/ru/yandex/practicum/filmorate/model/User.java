package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long id;
//    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,}", message = "Проверьте правильность ввода адреса электронной почты")
    @NotNull(message = "Проверьте правильность ввода адреса электронной почты: не может быть null")
    @Email(message = "Проверьте правильность ввода адреса электронной почты")
    private String email;

    @NotNull(message = "Проверьте правильность ввода логина: не может быть null")
    @Pattern(regexp = "^\\S+$", message = "Проверьте правильность ввода логина: не должен содержать пробелы или быть пустым")
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    private Set<Long> friends;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!Objects.equals(email, user.email)) return false;
        if (!Objects.equals(login, user.login)) return false;
        if (!Objects.equals(name, user.name)) return false;
        if (!Objects.equals(birthday, user.birthday)) return false;
        return Objects.equals(friends, user.friends);
    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
        result = 31 * result + (friends != null ? friends.hashCode() : 0);
        return result;
    }
}
