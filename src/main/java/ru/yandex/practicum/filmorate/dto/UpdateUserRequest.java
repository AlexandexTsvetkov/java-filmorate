package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {

    @NotNull(message = "Id пользователя должен быть задан")
    @Positive(message = "Некорректное значение id")
    private long id;

    @Email
    private String email;

    @NotBlank(message = "Логин не может быть пустым или содержать только пробелы")
    private String login;

    private String name;

    @Past(message = "Дата должна быть в прошлом")
    private LocalDate birthday;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }

    public boolean hasLogin() {
        return !(login == null || login.isBlank());
    }

    public boolean hasBirthday() {
        return !(birthday == null);
    }
}