package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class User {

    private long id;

    @Email
    private String email;

    @NotBlank(message = "Логин не может быть пустым или содержать только пробелы")
    @Pattern(regexp = "^\\S+$", message = "Имя не должно содержать пробелы")
    private String login;

    private String name;

    @NotNull(message = "Дата рождения должна быть задана")
    @Past(message = "Дата должна быть в прошлом")
    private LocalDate birthday;
    private Set<Long> friends;
}
