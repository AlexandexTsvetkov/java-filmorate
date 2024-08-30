package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    @Autowired
    private final UserDbStorage userStorage;
    @Autowired
    private final FilmDbStorage filmStorage;

    @Test
    public void testFindUserById() {

        User newUser = new User();
        newUser.setName("test");
        newUser.setLogin("test");
        newUser.setEmail("test@mail.ru");
        newUser.setBirthday(LocalDate.of(1994, 1, 1));

        User user = userStorage.create(newUser);

        Optional<User> userOptional = userStorage.getUser(user.getId());

        Assertions.assertTrue(userOptional.isPresent());

        Assertions.assertEquals(userOptional.get().getId(), user.getId());
    }

    @Test
    public void testCreateAndFindAllUsers() {

        int size = userStorage.findAll().size();

        User newUser = new User();
        newUser.setName("test");
        newUser.setLogin("test");
        newUser.setEmail("test@mail.ru");
        newUser.setBirthday(LocalDate.of(1994, 1, 1));

        User user = userStorage.create(newUser);

        Assertions.assertEquals(size + 1, userStorage.findAll().size());
    }

    @Test
    public void testUpdateUser() {

        User newUser = new User();
        newUser.setName("test");
        newUser.setLogin("test");
        newUser.setEmail("test@mail.ru");
        newUser.setBirthday(LocalDate.of(1994, 1, 1));

        User user = userStorage.create(newUser);

        Optional<User> userOptional = userStorage.getUser(user.getId());

        user.setName("newName");

        userStorage.update(user);

        Optional<User> updatedUserOptional = userStorage.getUser(user.getId());

        Assertions.assertTrue(updatedUserOptional.isPresent());

        Assertions.assertEquals(user.getName(), updatedUserOptional.get().getName());
    }

    @Test
    public void testAddFriend() {

        User newUser = new User();
        newUser.setName("test");
        newUser.setLogin("test");
        newUser.setEmail("test@mail.ru");
        newUser.setBirthday(LocalDate.of(1994, 1, 1));

        User user = userStorage.create(newUser);

        long userId = user.getId();

        User newUser2 = new User();
        newUser2.setName("test");
        newUser2.setLogin("test");
        newUser2.setEmail("test@mail.ru");
        newUser2.setBirthday(LocalDate.of(1994, 1, 1));

        User user2 = userStorage.create(newUser2);

        long user2Id = user2.getId();

        userStorage.addFriend(userId, user2Id);

        Collection<User> friends = userStorage.getFriends(user2Id);
        Assertions.assertEquals(1, friends.size());
        Assertions.assertEquals(userId, friends.iterator().next().getId());
    }

    @Test
    public void testDeleteFriend() {

        User newUser = new User();
        newUser.setName("test");
        newUser.setLogin("test");
        newUser.setEmail("test@mail.ru");
        newUser.setBirthday(LocalDate.of(1994, 1, 1));

        User user = userStorage.create(newUser);

        long userId = user.getId();

        User newUser2 = new User();
        newUser2.setName("test");
        newUser2.setLogin("test");
        newUser2.setEmail("test@mail.ru");
        newUser2.setBirthday(LocalDate.of(1994, 1, 1));

        User user2 = userStorage.create(newUser2);

        long user2Id = user2.getId();

        userStorage.addFriend(userId, user2Id);

        Collection<User> friends = userStorage.getFriends(user2Id);
        Assertions.assertEquals(1, friends.size());
        Assertions.assertEquals(userId, friends.iterator().next().getId());

        userStorage.deleteFriend(userId, user2Id);

        Collection<User> friends2 = userStorage.getFriends(user2Id);
        Assertions.assertEquals(0, friends2.size());
    }

    @Test
    public void testGetCommonFriends() {

        User newUser = new User();
        newUser.setName("test");
        newUser.setLogin("test");
        newUser.setEmail("test@mail.ru");
        newUser.setBirthday(LocalDate.of(1994, 1, 1));

        User user = userStorage.create(newUser);

        long userId = user.getId();

        User newUser2 = new User();
        newUser2.setName("test");
        newUser2.setLogin("test");
        newUser2.setEmail("test@mail.ru");
        newUser2.setBirthday(LocalDate.of(1994, 1, 1));

        User user2 = userStorage.create(newUser2);

        long user2Id = user2.getId();

        User newUser3 = new User();
        newUser3.setName("test");
        newUser3.setLogin("test");
        newUser3.setEmail("test@mail.ru");
        newUser3.setBirthday(LocalDate.of(1994, 1, 1));

        User user3 = userStorage.create(newUser3);

        long user3Id = user3.getId();

        userStorage.addFriend(userId, user2Id);
        userStorage.addFriend(userId, user3Id);

        Collection<User> commonFriends = userStorage.getCommonFriends(user2Id, user3Id);
        Assertions.assertEquals(1, commonFriends.size());
        Assertions.assertEquals(userId, commonFriends.iterator().next().getId());
    }

    @Test
    public void testFindFilmById() {

        Film film = new Film();
        film.setName("testFilm");
        film.setDescription("testDescription");
        film.setReleaseDate(LocalDate.of(1994, 1, 1));
        film.setDuration(100);
        film.setMpa(new Rating(1, "someRating"));

        film = filmStorage.create(film);

        Optional<Film> filmOptional = filmStorage.getFilm(film.getId());

        Assertions.assertTrue(filmOptional.isPresent());

        Assertions.assertEquals(filmOptional.get().getId(), film.getId());
    }

    @Test
    public void testCreateAndFindAllFilms() {

        int size = filmStorage.findAll().size();

        Film film = new Film();
        film.setName("testFilm");
        film.setDescription("testDescription");
        film.setReleaseDate(LocalDate.of(1994, 1, 1));
        film.setDuration(100);
        film.setMpa(new Rating(1, "someRating"));

        film = filmStorage.create(film);

        Assertions.assertEquals(size + 1, filmStorage.findAll().size());
    }

    @Test
    public void testAddLike() {

        Film film = new Film();
        film.setName("testFilm");
        film.setDescription("testDescription");
        film.setReleaseDate(LocalDate.of(1994, 1, 1));
        film.setDuration(100);
        film.setMpa(new Rating(1, "someRating"));

        film = filmStorage.create(film);

        User newUser = new User();
        newUser.setName("test");
        newUser.setLogin("test");
        newUser.setEmail("test@mail.ru");
        newUser.setBirthday(LocalDate.of(1994, 1, 1));

        User user = userStorage.create(newUser);

        long userId = user.getId();

        filmStorage.addLike(film.getId(), userId);

        List<Long> likes = filmStorage.getLikes(film.getId());

        Assertions.assertEquals(1, likes.size());

        Assertions.assertEquals(likes.getFirst(), userId);
    }

    @Test
    public void testDeleteLike() {

        Film film = new Film();
        film.setName("testFilm");
        film.setDescription("testDescription");
        film.setReleaseDate(LocalDate.of(1994, 1, 1));
        film.setDuration(100);
        film.setMpa(new Rating(1, "someRating"));

        film = filmStorage.create(film);

        User newUser = new User();
        newUser.setName("test");
        newUser.setLogin("test");
        newUser.setEmail("test@mail.ru");
        newUser.setBirthday(LocalDate.of(1994, 1, 1));

        User user = userStorage.create(newUser);

        long userId = user.getId();

        filmStorage.addLike(film.getId(), userId);

        List<Long> likes = filmStorage.getLikes(film.getId());

        Assertions.assertEquals(1, likes.size());

        Assertions.assertEquals(likes.getFirst(), userId);

        filmStorage.deleteLike(film.getId(), userId);

        List<Long> likes2 = filmStorage.getLikes(film.getId());

        Assertions.assertEquals(0, likes2.size());
    }

    @Test
    public void testUpdateFilm() {

        Film film = new Film();
        film.setName("testFilm");
        film.setDescription("testDescription");
        film.setReleaseDate(LocalDate.of(1994, 1, 1));
        film.setDuration(100);
        film.setMpa(new Rating(1, "someRating"));

        film = filmStorage.create(film);

        Optional<Film> filmOptional = filmStorage.getFilm(film.getId());

        film.setName("newName");

        filmStorage.update(film);

        Optional<Film> updatedFilmOptional = filmStorage.getFilm(film.getId());

        Assertions.assertTrue(updatedFilmOptional.isPresent());

        Assertions.assertEquals(film.getName(), updatedFilmOptional.get().getName());
    }

    @Test
    public void testGetPopular() {

        Film film = new Film();
        film.setName("testFilm");
        film.setDescription("testDescription");
        film.setReleaseDate(LocalDate.of(1994, 1, 1));
        film.setDuration(100);
        film.setMpa(new Rating(1, "someRating"));

        film = filmStorage.create(film);

        User newUser = new User();
        newUser.setName("test");
        newUser.setLogin("test");
        newUser.setEmail("test@mail.ru");
        newUser.setBirthday(LocalDate.of(1994, 1, 1));

        User user = userStorage.create(newUser);

        long userId = user.getId();

        filmStorage.addLike(film.getId(), userId);

        Collection<Film> popularFilms = filmStorage.getPopular(1);

        Assertions.assertEquals(1, popularFilms.size());
    }
}
