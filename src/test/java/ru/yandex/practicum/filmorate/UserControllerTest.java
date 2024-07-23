package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.advice.GlobalExceptionHandler;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.servise.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserStorage userStorage;

    @Autowired
    private UserController userController;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testCreateUser() throws Exception {
        String userJson = "{\n  \"login\": \"dolore\",\n  \"name\": \"Nick Name\",\n  \"email\": \"mail@mail.ru\",\n  \"birthday\": \"1946-08-20\"\n}";

        MvcResult result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        User user = objectMapper.readValue(responseBody, User.class);

        Assertions.assertEquals(1L, user.getId());
        Assertions.assertEquals("mail@mail.ru", user.getEmail());
        Assertions.assertEquals("dolore", user.getLogin());
        Assertions.assertEquals("Nick Name", user.getName());
        Assertions.assertEquals(LocalDate.of(1946, 8, 20), user.getBirthday());
    }

    @Test
    void testValidateBirthday() throws Exception {
        String userJson = "{\n  \"login\": \"dolore\",\n  \"name\": \"Nick Name\",\n  \"email\": \"mail@mail.ru\",\n  \"birthday\": \"2025-08-20\"\n}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testValidateEmail() throws Exception {
        String userJson = "{\n  \"login\": \"dolore\",\n  \"name\": \"Nick Name\",\n  \"email\": \"mail@\",\n  \"birthday\": \"2000-08-20\"\n}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testValidateLoginContainsSpace() throws Exception {
        String userJson = "{\n  \"login\": \"dol ore\",\n  \"name\": \"Nick Name\",\n  \"email\": \"mail@yandex.ru\",\n  \"birthday\": \"2000-08-20\"\n}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testValidateLoginIsEmpty() throws Exception {
        String userJson = "{\n  \"login\": \"\",\n  \"name\": \"Nick Name\",\n  \"email\": \"mail@yandex.ru\",\n  \"birthday\": \"2000-08-20\"\n}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testValidateLoginIsNull() throws Exception {
        String userJson = "{\n  \"name\": \"Nick Name\",\n  \"email\": \"mail@yandex.ru\",\n  \"birthday\": \"2000-08-20\"\n}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    void testNameIsNull() throws Exception {
        String userJson = "{\n  \"login\": \"dolore\",\n \"email\": \"mail@mail.ru\",\n  \"birthday\": \"1946-08-20\"\n}";

        MvcResult result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        User user = objectMapper.readValue(responseBody, User.class);

        Assertions.assertEquals("dolore", user.getName());
    }

    @Test
    void testNotFound() throws Exception {
        String userJson = "{\n  \"login\": \"dolore\",\n  \"id\": 1000,\n  \"name\": \"Nick Name\",\n  \"email\": \"mail@mail.ru\",\n  \"birthday\": \"1946-08-20\"\n}";

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEmptyRequest() throws Exception {

        mockMvc.perform(post("/users"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUsers() throws Exception {
        String userJson = "{\n  \"login\": \"dolore\",\n  \"name\": \"Nick Name\",\n  \"email\": \"mail@mail.ru\",\n  \"birthday\": \"1946-08-20\"\n}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        User[] users = objectMapper.readValue(responseBody, User[].class);

        Assertions.assertEquals(1, users.length);
    }
}