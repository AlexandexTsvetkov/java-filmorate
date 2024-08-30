package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NewFilmRequest {

    @NotBlank(message = "Название не может быть пустым или содержать только пробелы")
    private String name;

    @Size(max = 200, message = "Длина должна быть не более 200 символов")
    private String description;

    @NotNull(message = "Дата релиза должна быть задана")
    private LocalDate releaseDate;

    @Positive(message = "Значение должно быть положительным")
    private int duration;

    private GenreNewFilmRequest[] genres;

    private RatingNewFilmRequest mpa;
}