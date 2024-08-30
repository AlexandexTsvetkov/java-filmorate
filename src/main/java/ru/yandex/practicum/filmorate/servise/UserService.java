package ru.yandex.practicum.filmorate.servise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Optional;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<UserDto> findAll() {
        return userStorage.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto findById(Long id) {
        Optional<User> user = userStorage.getUser(id);

        if (user.isPresent()) {
            return UserMapper.mapToUserDto(user.get());
        }

        throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", id));
    }

    public UserDto create(NewUserRequest newUserRequest) {

        String userName = newUserRequest.getName();
        if (userName == null || userName.isBlank()) {
            newUserRequest.setName(newUserRequest.getLogin());
        }

        User newUser = UserMapper.mapToUser(newUserRequest);
        return UserMapper.mapToUserDto(userStorage.create(newUser));
    }

    public UserDto update(UpdateUserRequest updateUserRequest) {

        long userId = updateUserRequest.getId();

        Optional<User> optionalUpdatedUser = userStorage.getUser(userId);

        if (optionalUpdatedUser.isPresent()) {

            User user = optionalUpdatedUser.get();

            UserMapper.updateUserFields(user, updateUserRequest);

            User updatedUser = userStorage.update(user);
            return UserMapper.mapToUserDto(updatedUser);
        } else {
            throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", userId));
        }
    }

    public void addFriend(long friendId, long id) {

        if (userStorage.getUser(id).isEmpty()) {
            throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", id));
        }

        if (userStorage.getUser(friendId).isEmpty()) {
            throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", friendId));
        }

        userStorage.addFriend(id, friendId);
    }

    public void deteteFriend(long friendId, long id) {

        if (userStorage.getUser(id).isEmpty()) {
            throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", id));
        }

        if (userStorage.getUser(friendId).isEmpty()) {
            throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", friendId));
        }

        userStorage.deleteFriend(id, friendId);
    }

    public Collection<UserDto> getFriends(long id) {

        if (userStorage.getUser(id).isPresent()) {
            return userStorage.getFriends(id).stream()
                    .map(UserMapper::mapToUserDto)
                    .toList();
        }
        throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", id));
    }

    public Collection<UserDto> getCommonFriends(long id, long otherId) {

        if (userStorage.getUser(id).isEmpty()) {
            throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", id));
        }

        if (userStorage.getUser(otherId).isEmpty()) {
            throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", otherId));
        }

        return userStorage.getCommonFriends(id, otherId).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }
}
