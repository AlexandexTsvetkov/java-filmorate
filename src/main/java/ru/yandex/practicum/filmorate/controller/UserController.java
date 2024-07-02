package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {

        String userName = user.getName();
        if (userName == null || userName.isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());

        users.put(user.getId(), user);

        log.info("Добавлен пользователь {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) throws NoSuchMethodException, ValidationException, NotFoundException {

        Long id = newUser.getId();

        if (id == 0) {
            throw new ValidationException(
                    new MethodParameter(this.getClass().getMethod("update", User.class), 0),
                    new BeanPropertyBindingResult(newUser, "user"));
        }
        if (users.containsKey(id)) {

            User oldUser = users.get(id);

            String newUserName = newUser.getName();
            String newLogin = newUser.getLogin();

            oldUser.setName((newUserName == null || newUserName.isBlank()) ? newLogin : newUserName);
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setBirthday(newUser.getBirthday());

            log.info("Обновлен пользователь {}", oldUser);
            return newUser;
        }
        throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", id));
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}