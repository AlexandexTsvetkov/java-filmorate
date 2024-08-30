package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateFilmRequest {

    @NotNull(message = "Id пользователя должен быть задан")
    @Positive(message = "Некорректное значение id")
    private long id;

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

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasReleaseDate() {
        return !(releaseDate == null);
    }

    public boolean hasGenres() {
        return !(genres == null);
    }

    public boolean hasMpa() {
        return !(mpa == null);
    }
}