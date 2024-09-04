package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("UserDbStorage")
public class UserDbStorage extends BaseRepository<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT ID, EMAIL, LOGIN, NAME, BIRTHDAY FROM \"USER\"";
    private static final String FIND_BY_ID_QUERY = "SELECT ID, EMAIL, LOGIN, NAME, BIRTHDAY FROM \"USER\" WHERE ID = ?";
    private static final String INSERT_QUERY = "INSERT INTO \"USER\"(EMAIL, LOGIN, NAME, BIRTHDAY)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE \"USER\" SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE ID = ?";
    private static final String FIND_ALL_FRIENDS_QUERY = "SELECT ID, EMAIL, LOGIN, NAME, BIRTHDAY FROM \"USER\" " +
            "WHERE ID IN (SELECT FOLLOWING_USER_ID FRIEND_ID FROM USER_RELATIONSHIP " +
            "WHERE FOLLOWED_USER_ID = ? " +
            "UNION " +
            "SELECT FOLLOWED_USER_ID FRIEND_ID FROM USER_RELATIONSHIP " +
            "WHERE FOLLOWING_USER_ID = ? AND CONFIRMED)";
    private static final String FIND_ALL_COMMON_FRIENDS_QUERY = "SELECT ID, EMAIL, LOGIN, NAME, BIRTHDAY FROM \"USER\" " +
            "WHERE ID IN (SELECT FOLLOWING_USER_ID FRIEND_ID FROM USER_RELATIONSHIP " +
            "WHERE FOLLOWED_USER_ID = ? " +
            "UNION " +
            "SELECT FOLLOWED_USER_ID FRIEND_ID FROM USER_RELATIONSHIP " +
            "WHERE FOLLOWING_USER_ID = ? AND CONFIRMED) " +
            "AND ID IN (SELECT FOLLOWING_USER_ID FRIEND_ID FROM USER_RELATIONSHIP " +
            "WHERE FOLLOWED_USER_ID = ? " +
            "UNION " +
            "SELECT FOLLOWED_USER_ID FRIEND_ID FROM USER_RELATIONSHIP " +
            "WHERE FOLLOWING_USER_ID = ? AND CONFIRMED) ";

    private static final String FIND_FRIENDS_QUERY = "SELECT ID, EMAIL, LOGIN, NAME, BIRTHDAY FROM \"USER\" " +
            "WHERE ID IN (SELECT FOLLOWING_USER_ID FRIEND_ID FROM USER_RELATIONSHIP " +
            "WHERE FOLLOWED_USER_ID = ? AND CONFIRMED " +
            "UNION " +
            "SELECT FOLLOWED_USER_ID FRIEND_ID FROM USER_RELATIONSHIP " +
            "WHERE FOLLOWING_USER_ID = ? AND CONFIRMED )";
    private static final String FIND_FOLLOWERS_QUERY = "SELECT * FROM \"USER\" " +
            "WHERE ID IN (SELECT FOLLOWING_USER_ID FRIEND_ID FROM USER_RELATIONSHIP " +
            "WHERE FOLLOWED_USER_ID = ? AND NOT CONFIRMED )";
    private static final String INSERT_FRIEND_QUERY = "INSERT INTO USER_RELATIONSHIP (FOLLOWING_USER_ID, FOLLOWED_USER_ID)" +
            "VALUES (?, ?)";
    private static final String CONFIRM_FRIEND_QUERY = "UPDATE USER_RELATIONSHIP SET CONFIRMED = TRUE WHERE FOLLOWING_USER_ID = ? AND FOLLOWED_USER_ID = ?";
    private static final String FIND_FOLLOWS_QUERY = "SELECT * FROM \"USER\" " +
            "WHERE ID IN (SELECT FOLLOWED_USER_ID FRIEND_ID FROM USER_RELATIONSHIP " +
            "WHERE FOLLOWING_USER_ID = ?)";
    private static final String DELETE_RELATIONSHIP_QUERY = "DELETE FROM USER_RELATIONSHIP WHERE FOLLOWING_USER_ID = ? AND FOLLOWED_USER_ID = ?";


    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper, User.class);
    }

    @Override
    public Collection<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<User> getUser(long userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    @Override
    public User create(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public Collection<User> getFriends(long userId) {
        return findMany(FIND_ALL_FRIENDS_QUERY, userId, userId);
    }

    public List<User> getFollowers(Object... params) {
        return findMany(FIND_FOLLOWERS_QUERY, params);
    }

    public List<User> getFollows(Object... params) {
        return findMany(FIND_FOLLOWS_QUERY, params);
    }

    @Override
    public void addFriend(long id, long friendId) {

        if (getFriends(id).stream()
                .map(User::getId)
                .filter(userId -> userId == friendId)
                .findFirst().isEmpty()) {
            if (getFollowers(id).stream()
                    .map(User::getId)
                    .anyMatch(userId -> userId == friendId)) {

                int rowsUpdated = jdbc.update(CONFIRM_FRIEND_QUERY, id, friendId);
                if (rowsUpdated == 0) {
                    throw new InternalServerException("Не удалось обновить данные");
                }
            } else if (getFollows(id).stream()
                    .map(User::getId)
                    .filter(userId -> userId == friendId)
                    .findFirst().isEmpty()) {
                int rowsUpdated = jdbc.update(INSERT_FRIEND_QUERY, id, friendId);
                if (rowsUpdated == 0) {
                    throw new InternalServerException("Не удалось обновить данные");
                }
            }
        }
    }

    @Override
    public void deleteFriend(long id, long friendId) {

        if (getFollows(id).stream()
                .map(User::getId)
                .anyMatch(userId -> userId == friendId)) {

            int rowsUpdated = jdbc.update(DELETE_RELATIONSHIP_QUERY, id, friendId);
            if (rowsUpdated == 0) {
                throw new InternalServerException("Не удалось обновить данные");
            }
        }
//        } else if (getFollows(friendId).stream()
//                .map(User::getId)
//                .anyMatch(userId -> userId == id)) {
//            int rowsUpdated = jdbc.update(DELETE_RELATIONSHIP_QUERY, friendId, id);
//            if (rowsUpdated == 0) {
//                throw new InternalServerException("Не удалось обновить данные");
//            }
//        }
    }

    public Collection<User> getCommonFriends(long id, long otherId) {
        return findMany(FIND_ALL_COMMON_FRIENDS_QUERY, id, otherId, otherId, id);
    }
}
