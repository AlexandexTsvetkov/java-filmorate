package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
    private long counter = 0L;

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {

        log.info("пришел Post запрос /users с телом: {}", user);

        String userName = user.getName();
        if (userName == null || userName.isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());

        users.put(user.getId(), user);

        log.info("Отправлен ответ Post /users с телом: {}", user);

        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {

        log.info("пришел PUT запрос /users с телом: {}", newUser);

        long id = newUser.getId();

        if (users.containsKey(id)) {

            String userName = newUser.getName();
            if (userName == null || userName.isBlank()) {
                newUser.setName(newUser.getLogin());
            }

            users.put(id, newUser);

            log.info("Отправлен ответ PUT /users с телом: {}", newUser);

            return newUser;
        }
        throw new NotFoundException(MessageFormat.format("Пользователь с id {0, number} не найден", id));
    }

    private long getNextId() {
        return ++counter;
    }
}