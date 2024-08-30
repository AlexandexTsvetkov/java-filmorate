package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseRepository;


import java.util.List;

@Repository
public class GenreDbStorage extends BaseRepository<Genre> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM GENRE";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM GENRE WHERE ID = ?";
    private static final String FIND_BY_ID_ARRAY_QUERY = "SELECT * FROM GENRE WHERE ID IN (:IDS)";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT G.* FROM GENRE G" +
            " INNER JOIN FILM_GENRE F ON G.ID = F.GENRE_ID WHERE F.FILM_ID = ?";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper, Genre.class);
    }

    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Genre getGenre(int genreId) {
        return findOne(FIND_BY_ID_QUERY, genreId).orElse(null);
    }

    public List<Genre> findAllByIdArray(int[] idArray) {
        return findMany(FIND_BY_ID_ARRAY_QUERY, new MapSqlParameterSource("IDS", idArray));
    }

    public List<Genre> findAllByFilmId(long filmId) {
        return jdbc.query(FIND_BY_FILM_ID_QUERY, mapper, filmId);
    }
}
