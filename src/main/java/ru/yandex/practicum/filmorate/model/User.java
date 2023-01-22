package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;

import ru.yandex.practicum.filmorate.utils.deserializers.UserDeserializer;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = UserDeserializer.class)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (!getEmail().equals(user.getEmail())) return false;
        if (!getLogin().equals(user.getLogin())) return false;
        if (!getName().equals(user.getName())) return false;

        return getBirthday().equals(user.getBirthday());
    }

    @Override
    public int hashCode() {
        int result = getEmail().hashCode();
        result = 31 * result + getLogin().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getBirthday().hashCode();
        return result;
    }
}
