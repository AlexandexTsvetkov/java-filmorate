package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long counter = 0L;

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {

        user.setId(getNextId());
        user.setFriends(new HashSet<>());

        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User update(User newUser) {

        long id = newUser.getId();

        newUser.setFriends(users.get(id).getFriends());
        users.put(id, newUser);

        return newUser;
    }

    @Override
    public void addFriend(long id, long friendId) {

        User user = users.get(id);
        User user2 = users.get(friendId);
        user.getFriends().add(friendId);
        user2.getFriends().add(id);
    }

    @Override
    public void deleteFriend(long id, long friendId) {

        User user = users.get(id);
        User user2 = users.get(friendId);
        user.getFriends().remove(friendId);
        user2.getFriends().remove(id);
    }

    @Override
    public Collection<User> getFriends(long id) {

        return users.get(id).getFriends().stream()
                .map(users::get)
                .collect(Collectors.toList());

    }

    @Override
    public Collection<User> getCommonFriends(long id, long otherId) {

        return users.get(id).getFriends().stream()
                .filter(users.get(otherId).getFriends()::contains)
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> getUser(long id) {

        User user = users.get(id);
        return (user == null) ? Optional.empty() : Optional.of(user);
    }

    private long getNextId() {
        return ++counter;
    }
}
