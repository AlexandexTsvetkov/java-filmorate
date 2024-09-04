package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film newFilm);

    void addLike(long id, long userId);

    void deleteLike(long id, long userId);

    Optional<Film> getFilm(long id);

    List<Long> getLikes(long id);

    Collection<Film> getPopular(int count);
}
