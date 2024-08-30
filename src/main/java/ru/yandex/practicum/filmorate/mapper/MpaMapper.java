package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.model.Rating;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MpaMapper {

    public static RatingDto mapToMpaDto(Rating rating) {
        RatingDto dto = new RatingDto();
        dto.setId(rating.getId());
        dto.setName(rating.getName());
        return dto;
    }
}