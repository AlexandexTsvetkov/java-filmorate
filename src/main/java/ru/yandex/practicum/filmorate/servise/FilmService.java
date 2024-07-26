package ru.yandex.practicum.filmorate.servise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Collection;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
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

        if (filmStorage.getFilm(newFilm.getId()) == null) {
            throw new NotFoundException(MessageFormat.format("Пост с id {0, number} не найден", newFilm.getId()));
        }

        return filmStorage.update(newFilm);
    }

    public void addLike(long id, long userId) {

        if (filmStorage.getFilm(id) != null) {

            if (userStorage.getUser(userId) != null) {
                filmStorage.addLike(id, userId);
                return;
            }
            throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", userId));
        }
        throw new NotFoundException(MessageFormat.format("Фильм с id {0, number} не найден", id));
    }

    public void deleteLike(long id, long userId) {

        if (filmStorage.getFilm(id) != null) {

            if (userStorage.getUser(userId) != null) {
                filmStorage.deleteLike(id, userId);
                return;
            }
            throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", userId));
        }
        throw new NotFoundException(MessageFormat.format("Фильм с id {0, number} не найден", id));
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
