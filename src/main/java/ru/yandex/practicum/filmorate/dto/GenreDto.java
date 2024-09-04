package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GenreDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int id;
    @NotBlank
    private String name;

}
