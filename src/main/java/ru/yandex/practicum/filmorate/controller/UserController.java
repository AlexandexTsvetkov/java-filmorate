package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.servise.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {

        log.info("пришел Get запрос /users");

        Collection<User> users = userService.findAll();

        log.info("Отправлен ответ Get /users с телом: {}", users);

        return users;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {

        log.info("пришел Post запрос /users с телом: {}", user);

        User newUser = userService.create(user);

        log.info("Отправлен ответ Post /users с телом: {}", newUser);

        return newUser;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {

        log.info("пришел PUT запрос /users с телом: {}", user);

        User newUser = userService.update(user);

        log.info("Отправлен ответ PUT /users с телом: {}", newUser);

        return newUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.deteteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable long id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}