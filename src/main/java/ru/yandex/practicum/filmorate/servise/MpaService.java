package ru.yandex.practicum.filmorate.servise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingDbStorage;

import java.text.MessageFormat;
import java.util.Collection;

@Service
public class MpaService {

    private final RatingDbStorage ratingDbStorage;

    @Autowired
    public MpaService(RatingDbStorage ratingDbStorage) {
        this.ratingDbStorage = ratingDbStorage;
    }

    public Collection<RatingDto> findAll() {
        return ratingDbStorage.findAll().stream()
                .map(MpaMapper::mapToMpaDto)
                .toList();
    }

    public RatingDto findById(int id) {

        Rating rating = ratingDbStorage.getRating(id);

        if (rating != null) {
            return MpaMapper.mapToMpaDto(rating);
        }

        throw new NotFoundException(MessageFormat.format("Рейтинг с id {0, number} не найден", id));
    }
}
