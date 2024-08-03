package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class Film {

    private long id;

    @NotBlank(message = "Название не может быть пустым или содержать только пробелы")
    private String name;

    @Size(max = 200, message = "Длина должна быть не более 200 символов")
    private String description;

    @NotNull(message = "Дата релиза должна быть задана")
    private LocalDate releaseDate;

    @Positive(message = "Значение должно быть положительным")
    private int duration;
    @JsonIgnore
    private Set<Long> likes;

}
