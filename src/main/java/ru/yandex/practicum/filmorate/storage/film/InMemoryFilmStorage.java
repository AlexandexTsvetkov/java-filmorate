package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films;
    private long counter;

    @Autowired
    public InMemoryFilmStorage() {
        this.counter = 0L;
        this.films = new HashMap<>();
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {

        film.setId(getNextId());

        film.setLikes(new HashSet<>());

        films.put(film.getId(), film);

        return film;
    }

    @Override
    public Film update(Film newFilm) {

        long id = newFilm.getId();

        newFilm.setLikes(films.get(id).getLikes());

        films.put(id, newFilm);

        return newFilm;
    }

    @Override
    public void addLike(long id, long friendId) {
        films.get(id).getLikes().add(friendId);
    }

    @Override
    public void deleteLike(long id, long friendId) {
        films.get(id).getLikes().remove(friendId);
    }

    @Override
    public Collection<Film> getPopular(int count) {
        return films.values().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(Integer.min(count, films.size()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Long> getLikes(long id) {
        return films.get(id).getLikes();
    }

    @Override
    public Film getFilm(long id) {
        return films.get(id);
    }

    private long getNextId() {
        return ++counter;
    }
}
