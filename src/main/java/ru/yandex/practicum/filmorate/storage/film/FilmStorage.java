package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film newFilm);

    void addLike(long id, long userId);

    void deleteLike(long id, long userId);

    Film getFilm(long id);

    Collection<Long> getLikes(long id);

    Collection<Film> getPopular(int count);
}
