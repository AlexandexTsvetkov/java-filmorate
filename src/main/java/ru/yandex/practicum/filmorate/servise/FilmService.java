package ru.yandex.practicum.filmorate.servise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreNewFilmRequest;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingDbStorage;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.*;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final RatingDbStorage ratingDbStorage;
    private final GenreDbStorage genreDbStorage;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage, UserService userService, RatingDbStorage ratingDbStorage, GenreDbStorage genreDbStorage) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.ratingDbStorage = ratingDbStorage;
        this.genreDbStorage = genreDbStorage;
    }

    public Collection<FilmDto> findAll() {
        Collection<Film> films = filmStorage.findAll();
        loadGenres(films);

        return films.stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public FilmDto create(NewFilmRequest newFilmRequest) {

        validateFilm(newFilmRequest);

        Rating rating = null;

        if (newFilmRequest.getMpa() != null) {
            rating = ratingDbStorage.getRating(newFilmRequest.getMpa().getId());
            if (rating == null) {
                throw new ValidationException(MessageFormat.format("Рейтинг с id {0, number} не найден", newFilmRequest.getMpa().getId()));
            }
        }

        List<Genre> genries = null;

        if (newFilmRequest.getGenres() != null) {

            genries = genreDbStorage.findAllByIdArray((Arrays.stream(newFilmRequest.getGenres()).map(GenreNewFilmRequest::getId).mapToInt(Integer::intValue).toArray()));

            checkGenres(genries, newFilmRequest.getGenres());
        }
        Film newFilm = FilmMapper.mapToFilm(newFilmRequest, genries, rating);

        return FilmMapper.mapToFilmDto(filmStorage.create(newFilm));
    }

    public FilmDto update(UpdateFilmRequest updateFilmRequest) {

        Optional<Film> oldFilmOptional = filmStorage.getFilm(updateFilmRequest.getId());

        if (oldFilmOptional.isEmpty()) {
            throw new NotFoundException(MessageFormat.format("Пост с id {0, number} не найден", updateFilmRequest.getId()));
        }

        Film oldFilm = oldFilmOptional.get();

        oldFilm.setGenres(genreDbStorage.findAllByFilmId(oldFilm.getId()));

        Rating rating = null;

        if (updateFilmRequest.getMpa() != null) {
            rating = ratingDbStorage.getRating(updateFilmRequest.getMpa().getId());
        }

        List<Genre> genries = null;

        if (updateFilmRequest.getGenres() != null) {

            genries = genreDbStorage.findAllByIdArray((Arrays.stream(updateFilmRequest.getGenres()).map(GenreNewFilmRequest::getId).toList()).stream().mapToInt(Integer::intValue).toArray());

            checkGenres(genries, updateFilmRequest.getGenres());
        }

        Film newFilm = FilmMapper.updateFilmFields(oldFilm, updateFilmRequest, genries, rating);

        if (updateFilmRequest.hasGenres()) {
            newFilm.setGenres(genries);
        }
        if (updateFilmRequest.hasMpa()) {
            newFilm.setMpa(rating);
        }

        return FilmMapper.mapToFilmDto(filmStorage.update(newFilm));
    }

    public void addLike(long id, long userId) {

        if (filmStorage.getFilm(id).isPresent()) {

            if (userService.findById(userId) != null) {
                filmStorage.addLike(id, userId);
                return;
            }
            throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", userId));
        }
        throw new NotFoundException(MessageFormat.format("Фильм с id {0, number} не найден", id));
    }

    public void deleteLike(long id, long userId) {

        if (filmStorage.getFilm(id).isPresent()) {

            if (userService.findById(userId) != null) {
                filmStorage.deleteLike(id, userId);
                return;
            }
            throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", userId));
        }
        throw new NotFoundException(MessageFormat.format("Фильм с id {0, number} не найден", id));
    }

    public Collection<FilmDto> getPopular(int count) {

        Collection<Film> films = filmStorage.getPopular(count);

        loadGenres(films);

        return films.stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public List<Long> getLikes(long id) {
        return filmStorage.getLikes(id);
    }

    public FilmDto getFilm(long id) {

        Optional<Film> filmOptional = filmStorage.getFilm(id);

        if (filmOptional.isEmpty()) {
            throw new NotFoundException(MessageFormat.format("Фильм с id {0, number} не найден", id));
        }

        Film film = filmOptional.get();

        List<Genre> genres = genreDbStorage.findAllByFilmId(film.getId());
        film.setGenres(genres);

        return FilmMapper.mapToFilmDto(film);
    }

    public void loadGenres(Collection<Film> films) {
        genreDbStorage.loadGenres(films);
    }

    private void validateFilm(NewFilmRequest data) {
        if (data.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Film release date invalid");
        }
    }

    private void checkGenres(List<Genre> genries, GenreNewFilmRequest[] genres) {
        List<Integer> requestGenresIds = Arrays.stream(genres).map(GenreNewFilmRequest::getId).toList();
        List<Integer> dbGenresIds = genries.stream().map(Genre::getId).toList();

        for (Integer requestGenreId : requestGenresIds) {

            if (!dbGenresIds.contains(requestGenreId)) {
                throw new ValidationException(MessageFormat.format("Жанр с id {0, number} не найден", requestGenreId));
            }
        }
    }
}
