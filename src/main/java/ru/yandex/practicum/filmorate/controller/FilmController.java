package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Long, Film> films = new HashMap<>();
    private long counter = 0L;

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {

        log.info("пришел Post запрос /films с телом: {}", film);

        validateFilm(film);

        film.setId(getNextId());

        films.put(film.getId(), film);

        log.info("Отправлен ответ Post /films с телом: {}", film);

        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {

        log.info("пришел PUT запрос /films с телом: {}", newFilm);

        long id = newFilm.getId();

        if (films.containsKey(id)) {

            validateFilm(newFilm);

            films.put(id, newFilm);

            log.info("Отправлен ответ PUT /films с телом: {}", newFilm);

            return newFilm;
        }
        throw new NotFoundException(MessageFormat.format("Пост с id {0, number} не найден", id));
    }

    private long getNextId() {
        return ++counter;
    }

    void validateFilm(Film data) {
        if (data.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Film release date invalid");
        }
    }
}