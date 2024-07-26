package ru.yandex.practicum.filmorate.servise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.text.MessageFormat;
import java.util.Collection;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {

        String userName = user.getName();
        if (userName == null || userName.isBlank()) {
            user.setName(user.getLogin());
        }

        return userStorage.create(user);
    }

    public User update(User newUser) {

        if (userStorage.getUser(newUser.getId()) != null) {

            String userName = newUser.getName();
            if (userName == null || userName.isBlank()) {
                newUser.setName(newUser.getLogin());
            }

            return userStorage.update(newUser);
        }
        throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", newUser.getId()));
    }

    public void addFriend(long id, long friendId) {

        if (userStorage.getUser(id) == null) {
            throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", id));
        }

        if (userStorage.getUser(friendId) == null) {
            throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", friendId));
        }

        userStorage.addFriend(id, friendId);
    }

    public void deteteFriend(long id, long friendId) {

        if (userStorage.getUser(id) == null) {
            throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", id));
        }

        if (userStorage.getUser(friendId) == null) {
            throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", friendId));
        }

        userStorage.deleteFriend(id, friendId);
    }

    public Collection<User> getFriends(long id) {

        if (userStorage.getUser(id) != null) {
            return userStorage.getFriends(id);
        }
        throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", id));
    }

    public Collection<User> getCommonFriends(long id, long otherId) {

        if (userStorage.getUser(id) == null) {
            throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", id));
        }

        if (userStorage.getUser(otherId) == null) {
            throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", otherId));
        }

        return userStorage.getCommonFriends(id, otherId);
    }
}
