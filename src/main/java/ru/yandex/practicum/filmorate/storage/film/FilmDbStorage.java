package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Qualifier("FilmDbStorage")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    private static final String FIND_ALL_QUERY = "SELECT FILM.*, RATING.NAME AS RATING_NAME FROM FILM " +
            " LEFT JOIN RATING ON FILM.RATING_ID = RATING.ID ";
    private static final String FIND_BY_ID_QUERY = "SELECT FILM.*, RATING.NAME AS RATING_NAME FROM FILM " +
            " LEFT JOIN RATING ON FILM.RATING_ID = RATING.ID " +
            "WHERE FILM.ID = ?";
    private static final String INSERT_QUERY = "INSERT INTO FILM(NAME, DESCRIPTION, RELEASEDATE, DURATION, RATING_ID)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_GENRES_QUERY = "INSERT INTO FILM_GENRE(FILM_ID, GENRE_ID)" +
            "VALUES (?, ?)";
    private static final String DELETE_GENRES_QUERY = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
    private static final String UPDATE_QUERY = "UPDATE FILM SET NAME = ?, DESCRIPTION = ?, RELEASEDATE = ?, DURATION = ?" +
            ", RATING_ID = ? WHERE ID = ?";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO \"LIKE\" (FILM_ID, USER_ID) " +
            "VALUES (?, ?) ";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM \"LIKE\" WHERE FILM_ID = ? AND USER_ID = ?";
    private static final String FIND_LIKE_QUERY = "SELECT FILM_ID, USER_ID FROM \"LIKE\" WHERE FILM_ID = ? AND USER_ID = ? LIMIT 1";
    private static final String FIND_LIKES_QUERY = "SELECT FILM_ID, USER_ID FROM \"LIKE\" WHERE FILM_ID = ?";
    private static final String FIND_POPULAR_QUERY = "SELECT FILM.*, RATING.NAME AS RATING_NAME, COUNT(\"LIKE\".USER_ID) AS LIKE_COUNT " +
            "FROM FILM " +
            "LEFT JOIN RATING ON FILM.RATING_ID = RATING.ID " +
            "LEFT JOIN \"LIKE\" ON FILM.ID = \"LIKE\".FILM_ID " +
            "GROUP BY FILM.ID, FILM.NAME, FILM.DESCRIPTION, FILM.RELEASEDATE, FILM.DURATION, FILM.RATING_ID, RATING_NAME " +
            "ORDER BY LIKE_COUNT DESC " +
            "LIMIT ?";


    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper, Film.class);
    }

    @Override
    public Collection<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Film> getFilm(long filmId) {
        return findOne(FIND_BY_ID_QUERY, filmId);
    }

    @Override
    public Film create(Film film) {
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);

        if (film.getGenres() != null) {

            for (Genre genre : film.getGenres()) {
                jdbc.update(
                        INSERT_GENRES_QUERY,
                        id,
                        genre.getId()
                );
            }

        }
        return film;
    }

    @Override
    public Film update(Film film) {

        long id = film.getId();

        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                id
        );

        jdbc.update(DELETE_GENRES_QUERY, id);

        if (film.getGenres() != null) {

            for (Genre genre : film.getGenres()) {
                jdbc.update(
                        INSERT_GENRES_QUERY,
                        id,
                        genre.getId()
                );
            }
        }
        return film;
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        update(
                DELETE_LIKE_QUERY,
                filmId,
                userId
        );
    }

    @Override
    public void addLike(long filmId, long userId) {

        List<Map<String, Object>> result = jdbc.queryForList(FIND_LIKE_QUERY, filmId, userId);

        if (result.isEmpty()) {

            jdbc.update(
                    INSERT_LIKE_QUERY,
                    filmId,
                    userId
            );
        }
    }

    @Override
    public List<Long> getLikes(long filmId) {
        return jdbc.query(FIND_LIKES_QUERY, (rs, rowNum) -> rs.getLong("USER_ID"), filmId);
    }

    @Override
    public Collection<Film> getPopular(int count) {
        return jdbc.query(FIND_POPULAR_QUERY, mapper, count);
    }
}
