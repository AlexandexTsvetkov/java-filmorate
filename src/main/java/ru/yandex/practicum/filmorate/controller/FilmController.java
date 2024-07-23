package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.servise.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("пришел Get запрос /films");
        Collection<Film> films = filmService.findAll();
        log.info("Отправлен ответ Get /films с телом: {}", films);
        return films;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {

        log.info("пришел Post запрос /films с телом: {}", film);
        Film newFilm = filmService.create(film);
        log.info("Отправлен ответ Post /films с телом: {}", newFilm);
        return newFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {

        log.info("пришел PUT запрос /films с телом: {}", film);
        Film newFilm = filmService.update(film);
        log.info("Отправлен ответ PUT /films с телом: {}", newFilm);
        return newFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        log.info("пришел Put запрос /films/{}/like/{}", id, userId);
        filmService.addLike(id, userId);
        log.info("Отправлен Put ответ 204 /films/{}/like/{}", id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.info("пришел Delete запрос /films/{}/like/{}", id, userId);
        filmService.deleteLike(id, userId);
        log.info("Отправлен Delete ответ 204 /films/{}/like/{}", id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        log.info("пришел Get запрос /films/popular?count={}", count);
        Collection<Film> films = filmService.getPopular(count);
        log.info("пришел Get ответ /films/popular с телом {}", films);
        return films;
    }
}
