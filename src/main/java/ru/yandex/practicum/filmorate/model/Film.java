package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {

    private long id;

    @NotNull(message = "Название должно быть задано")
    @NotBlank(message = "Название не может быть пустым или содержать только пробелы")
    private String name;

    @Size(max = 200, message = "Длина должна быть не более 200 символов")
    private String description;

    @NotNull(message = "Название должно быть задано")
    private LocalDate releaseDate;

    @Positive(message = "Значение должно быть положительным")
    private int duration;
}
