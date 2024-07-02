package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {

    private long id;

    @NotNull(message = "Email должен быть задан")
    @NotBlank(message = "Email не может быть пустым или содержать только пробелы")
    @Email
    private String email;

    @NotNull(message = "Логин должен быть задан")
    @NotBlank(message = "Логин не может быть пустым или содержать только пробелы")
    @Pattern(regexp = "^\\S+$", message = "Имя не должно содержать пробелы")
    private String login;

    private String name;

    @NotNull(message = "Дата рождения должна быть задана")
    @Past(message = "Дата должна быть в прошлом")
    private LocalDate birthday;
}
