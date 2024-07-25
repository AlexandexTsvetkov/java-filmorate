package ru.yandex.practicum.filmorate.servise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {

        validateFilm(film);

        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {

        validateFilm(newFilm);

        return filmStorage.update(newFilm);
    }

    public void addLike(long id, long userId) {
        filmStorage.addLike(id, userId);
    }

    public void deleteLike(long id, long userId) {
        filmStorage.deleteLike(id, userId);
    }

    public Collection<Film> getPopular(int count) {
        return filmStorage.getPopular(count);
    }

    private void validateFilm(Film data) {
        if (data.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Film release date invalid");
        }
    }

    public Collection<Long> getLikes(long id) {
        return filmStorage.getLikes(id);
    }
}
