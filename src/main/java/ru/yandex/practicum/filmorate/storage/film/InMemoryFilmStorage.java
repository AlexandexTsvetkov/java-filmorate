package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films;
    private long counter;
    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmStorage(UserStorage userStorage) {
        this.counter = 0L;
        this.films = new HashMap<>();
        this.userStorage = userStorage;
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

        if (films.containsKey(id)) {

            newFilm.setLikes(films.get(id).getLikes());

            films.put(id, newFilm);

            return newFilm;
        }
        throw new NotFoundException(MessageFormat.format("Пост с id {0, number} не найден", id));
    }

    @Override
    public void addLike(long id, long friendId) {

        if (films.containsKey(id)) {

            Film film = films.get(id);

            if (userStorage.getUser(friendId) != null) {
                film.getLikes().add(friendId);
                return;
            }
            throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", friendId));
        }
        throw new NotFoundException(MessageFormat.format("Пост с id {0, number} не найден", id));
    }

    @Override
    public void deleteLike(long id, long friendId) {

        if (films.containsKey(id)) {

            Film film = films.get(id);

            if (userStorage.getUser(friendId) != null) {
                film.getLikes().remove(friendId);
                return;
            }
            throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", friendId));
        }
        throw new NotFoundException(MessageFormat.format("Пост с id {0, number} не найден", id));
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

    private long getNextId() {
        return ++counter;
    }
}
