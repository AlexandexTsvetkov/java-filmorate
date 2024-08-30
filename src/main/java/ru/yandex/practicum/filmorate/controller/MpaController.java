package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.servise.MpaService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {

    MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public Collection<RatingDto> findAll() {

        log.info("пришел Get запрос /mpa");
        Collection<RatingDto> mpas = mpaService.findAll();
        log.info("Отправлен ответ Get /mpa с телом: {}", mpas);
        return mpas;
    }

    @GetMapping("/{id}")
    public RatingDto getById(@PathVariable int id) {

        log.info("пришел Get запрос /mpa/{}", id);
        RatingDto mpa = mpaService.findById(id);
        log.info("Отправлен ответ Get с телом {}", mpa);
        return mpa;
    }
}