package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.servise.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<FilmDto> findAll() {
        log.info("пришел Get запрос /films");
        Collection<FilmDto> films = filmService.findAll();
        log.info("Отправлен ответ Get /films с телом: {}", films);
        return films;
    }

    @PostMapping
    public FilmDto create(@Valid @RequestBody NewFilmRequest newFilmRequest) {

        log.info("пришел Post запрос /films с телом: {}", newFilmRequest);
        FilmDto newFilm = filmService.create(newFilmRequest);
        log.info("Отправлен ответ Post /films с телом: {}", newFilm);
        return newFilm;
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody UpdateFilmRequest updateFilmRequest) {

        log.info("пришел PUT запрос /films с телом: {}", updateFilmRequest);
        FilmDto newFilm = filmService.update(updateFilmRequest);
        log.info("Отправлен ответ PUT /films с телом: {}", newFilm);
        return newFilm;
    }

    @GetMapping("/{id}/likes")
    public List<Long> getLikes(@PathVariable long id) {
        log.info("пришел Get запрос /{}/likes", id);
        List<Long> likes = filmService.getLikes(id);
        log.info("Отправлен ответ Get /films с телом: {}", likes);
        return likes;
    }

    @GetMapping("/{id}")
    public FilmDto getFilm(@PathVariable long id) {
        log.info("пришел Get запрос /{}", id);
        FilmDto film = filmService.getFilm(id);
        log.info("Отправлен ответ Get /films с телом: {}", film);
        return film;
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
    public Collection<FilmDto> getPopular(@RequestParam(defaultValue = "10") int count) {
        log.info("пришел Get запрос /films/popular?count={}", count);
        Collection<FilmDto> films = filmService.getPopular(count);
        log.info("пришел Get ответ /films/popular с телом {}", films);
        return films;
    }
}
