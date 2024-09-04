package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {

    public static Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());

        return film;
    }

    public static Film mapToFilm(NewFilmRequest request, List<Genre> genries, Rating rating) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());

        if (genries != null) {
            film.setGenres(new ArrayList<>(genries));
        }
        if (rating != null) {
            film.setMpa(rating);
        }
        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setDescription(film.getDescription());
        dto.setName(film.getName());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());

        if (film.getMpa() != null) {
            dto.setMpa(MpaMapper.mapToMpaDto(film.getMpa()));
        }
        List<Genre> genres = film.getGenres();

        if (genres == null) {
            genres = new ArrayList<>();
        }
        dto.setGenres(genres.stream()
                .map(GenreMapper::mapToGenreDto)
                .toArray(GenreDto[]::new));

        return dto;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        if (request.hasName()) {
            film.setName(request.getName());
        }
        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }
        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }
        film.setDuration(request.getDuration());

        return film;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request, List<Genre> genries, Rating rating) {
        if (request.hasName()) {
            film.setName(request.getName());
        }
        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }
        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }
        film.setDuration(request.getDuration());

        if (genries != null) {
            film.setGenres(new ArrayList<>(genries));
        }
        if (rating != null) {
            film.setMpa(rating);
        }

        return film;
    }
}
