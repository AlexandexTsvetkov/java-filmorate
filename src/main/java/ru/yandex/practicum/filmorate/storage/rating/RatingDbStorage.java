package ru.yandex.practicum.filmorate.storage.rating;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.BaseRepository;


import java.util.List;

@Repository
public class RatingDbStorage extends BaseRepository<Rating> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM RATING";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM RATING WHERE id = ?";

    public RatingDbStorage(JdbcTemplate jdbc, RowMapper<Rating> mapper) {
        super(jdbc, mapper, Rating.class);
    }

    public List<Rating> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Rating getRating(int ratingId) {
        return findOne(FIND_BY_ID_QUERY, ratingId).orElse(null);
    }

}
