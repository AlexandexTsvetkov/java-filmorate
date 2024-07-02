package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.lang.reflect.Method;
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

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) throws NoSuchMethodException, ValidationException {

        validateFilm(film, this.getClass().getMethod("create", Film.class));

        film.setId(getNextId());

        films.put(film.getId(), film);

        log.debug("Добавлен фильм {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) throws NoSuchMethodException, ValidationException {

        Long id = newFilm.getId();

        if (id == 0) {
            throw new ValidationException(
                    new MethodParameter(this.getClass().getMethod("update", Film.class), 0),
                    new BeanPropertyBindingResult(newFilm, "film"));
        }
        if (films.containsKey(id)) {

            validateFilm(newFilm, this.getClass().getMethod("update", Film.class));

            Film oldFilm = films.get(id);

            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setDuration(newFilm.getDuration());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());

            log.info("Обновлен фильм {}", oldFilm);
            return newFilm;
        }
        throw new NotFoundException(MessageFormat.format("Пост с id {0, number} не найден", id));
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateFilm(Film film, Method method) throws ValidationException {

        LocalDate releaseDate = film.getReleaseDate();

        if (FilmController.MIN_RELEASE_DATE.isAfter(releaseDate)) {
            throw new ValidationException(
                    new MethodParameter(method, 0),
                    new BeanPropertyBindingResult(film, "film"));


        }
    }
}