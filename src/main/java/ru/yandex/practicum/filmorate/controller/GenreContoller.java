package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.servise.GenreService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreContoller {

    private final GenreService genreService;

    @Autowired
    public GenreContoller(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public Collection<GenreDto> findAll() {

        log.info("пришел Get запрос /genre");
        Collection<GenreDto> genres = genreService.findAll();
        log.info("Отправлен ответ Get /genre с телом: {}", genres);
        return genres;
    }

    @GetMapping("/{id}")
    public GenreDto getById(@PathVariable int id) {

        log.info("пришел Get запрос /genre/{}", id);
        GenreDto genre = genreService.findById(id);
        log.info("Отправлен ответ Get с телом {}", genre);
        return genre;
    }
}
