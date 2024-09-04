package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseRepository;


import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class GenreDbStorage extends BaseRepository<Genre> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM GENRE";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM GENRE WHERE ID = ?";
    private static final String FIND_BY_ID_ARRAY_QUERY = "SELECT G.* FROM GENRE G WHERE G.ID IN (#ids)";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT G.* FROM GENRE G" +
            " INNER JOIN FILM_GENRE F ON G.ID = F.GENRE_ID WHERE F.FILM_ID = ?";
    private static final String FIND_ALL_GENRES_OF_FILMS = "SELECT * FROM GENRE G, FILM_GENRE FG WHERE FG.GENRE_ID = G.ID AND FG.FILM_ID IN (#ids)";

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
        if (idArray.length == 0) {
            return Collections.emptyList();
        }
        String inSql = String.join(",", Collections.nCopies(idArray.length, "?"));
        return jdbc.query(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(FIND_BY_ID_ARRAY_QUERY.replace("#ids", inSql));
            for (int idx = 0; idx < idArray.length; idx++) {
                ps.setObject(idx + 1, idArray[idx]);
            }
            return ps;
        }, mapper);
    }

    public List<Genre> findAllByFilmId(long filmId) {
        return jdbc.query(FIND_BY_FILM_ID_QUERY, mapper, filmId);
    }

    public void loadGenres(Collection<Film> films) {
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        final Map<Long, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, film -> film));
        jdbc.query(FIND_ALL_GENRES_OF_FILMS.replace("#ids", inSql), (rs) -> {
            final Film film = filmById.get(rs.getLong("FILM_ID"));
            film.addGenre(mapper.mapRow(rs, 0));
        }, films.stream().map(Film::getId).toArray());
    }
}
