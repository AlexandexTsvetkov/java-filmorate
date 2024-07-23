package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long counter = 0L;

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {

        String userName = user.getName();
        if (userName == null || userName.isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        user.setFriends(new HashSet<>());

        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User update(User newUser) {

        long id = newUser.getId();

        if (users.containsKey(id)) {

            String userName = newUser.getName();
            if (userName == null || userName.isBlank()) {
                newUser.setName(newUser.getLogin());
            }

            newUser.setFriends(users.get(id).getFriends());
            users.put(id, newUser);

            return newUser;
        }
        throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", id));
    }

    @Override
    public void addFriend(long id, long friendId) {

        if (users.containsKey(id)) {

            User user = users.get(id);

            if (users.containsKey(friendId)) {

                User user2 = users.get(friendId);
                user.getFriends().add(friendId);
                user2.getFriends().add(id);

                return;
            }
            throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", friendId));
        }
        throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", id));
    }

    @Override
    public void deleteFriend(long id, long friendId) {

        if (users.containsKey(id)) {

            User user = users.get(id);

            if (users.containsKey(friendId)) {

                User user2 = users.get(friendId);
                user.getFriends().remove(friendId);
                user2.getFriends().remove(id);
                return;
            }
            throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", friendId));
        }
        throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", id));
    }

    @Override
    public Collection<User> getFriends(long id) {

        if (users.containsKey(id)) {

            User user = users.get(id);

            return user.getFriends().stream()
                    .map(users::get)
                    .collect(Collectors.toList());

        }
        throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", id));
    }

    @Override
    public Collection<User> getCommonFriends(long id, long otherId) {
        if (users.containsKey(id)) {

            User user = users.get(id);

            if (users.containsKey(otherId)) {

                Set<Long> otherUserFriendsId = users.get(otherId).getFriends();

                return user.getFriends().stream()
                        .filter(otherUserFriendsId::contains)
                        .map(users::get)
                        .collect(Collectors.toList());
            }
            throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", otherId));
        }
        throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", id));
    }

    @Override
    public User getUser(long id) {
        return users.get(id);
    }

    private long getNextId() {
        return ++counter;
    }
}
