package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.advice.GlobalExceptionHandler;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.servise.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private UserService userService;

    private UserStorage userStorage;

    private UserController userController;

    @BeforeEach
    void setUp() {

        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        UserController userController = new UserController(userService);

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

    @Test
    void testAddFriend() throws Exception {
        String userJson = "{\n  \"login\": \"dolore\",\n  \"name\": \"Nick Name\",\n  \"email\": \"mail@mail.ru\",\n  \"birthday\": \"1946-08-20\"\n}";

        MvcResult result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andReturn();

        String userJson2 = "{\n  \"login\": \"dolore2\",\n  \"name\": \"Nick Name2\",\n  \"email\": \"mail2@mail.ru\",\n  \"birthday\": \"1946-08-20\"\n}";

        MvcResult result2 = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson2))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result3 = mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isNoContent())
                .andReturn();

        MvcResult result4 = mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result4.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        User[] users = objectMapper.readValue(responseBody, User[].class);

        Assertions.assertEquals(1, users.length);
    }

    @Test
    void testDeleteFriend() throws Exception {
        String userJson = "{\n  \"login\": \"dolore\",\n  \"name\": \"Nick Name\",\n  \"email\": \"mail@mail.ru\",\n  \"birthday\": \"1946-08-20\"\n}";

        MvcResult result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andReturn();

        String userJson2 = "{\n  \"login\": \"dolore2\",\n  \"name\": \"Nick Name2\",\n  \"email\": \"mail2@mail.ru\",\n  \"birthday\": \"1946-08-20\"\n}";

        MvcResult result2 = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson2))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result3 = mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isNoContent())
                .andReturn();

        MvcResult result4 = mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result4.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        User[] users = objectMapper.readValue(responseBody, User[].class);

        Assertions.assertEquals(1, users.length);

        MvcResult result5 = mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isNoContent())
                .andReturn();

        MvcResult result6 = mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody2 = result6.getResponse().getContentAsString();
        users = objectMapper.readValue(responseBody2, User[].class);

        Assertions.assertEquals(0, users.length);
    }

    @Test
    void testGetFriends() throws Exception {
        String userJson = "{\n  \"login\": \"dolore\",\n  \"name\": \"Nick Name\",\n  \"email\": \"mail@mail.ru\",\n  \"birthday\": \"1946-08-20\"\n}";

        MvcResult result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andReturn();

        String userJson2 = "{\n  \"login\": \"dolore2\",\n  \"name\": \"Nick Name2\",\n  \"email\": \"mail2@mail.ru\",\n  \"birthday\": \"1946-08-20\"\n}";

        MvcResult result2 = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson2))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result3 = mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isNoContent())
                .andReturn();

        MvcResult result4 = mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result5 = mockMvc.perform(get("/users/2/friends"))
                .andExpect(status().isOk())
                .andReturn();


        String responseBody = result4.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        User[] users = objectMapper.readValue(responseBody, User[].class);

        String responseBody2 = result5.getResponse().getContentAsString();
        User[] users2 = objectMapper.readValue(responseBody2, User[].class);

        Assertions.assertEquals(1, users.length);
        Assertions.assertEquals(1, users2.length);
        Assertions.assertEquals(2L, users[0].getId());
        Assertions.assertEquals(1L, users2[0].getId());
    }

    @Test
    void testCommonFriends() throws Exception {
        String userJson = "{\n  \"login\": \"dolore\",\n  \"name\": \"Nick Name\",\n  \"email\": \"mail@mail.ru\",\n  \"birthday\": \"1946-08-20\"\n}";

        MvcResult result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andReturn();

        String userJson2 = "{\n  \"login\": \"dolore2\",\n  \"name\": \"Nick Name2\",\n  \"email\": \"mail2@mail.ru\",\n  \"birthday\": \"1946-08-20\"\n}";

        MvcResult result2 = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson2))
                .andExpect(status().isOk())
                .andReturn();

        String userJson3 = "{\n  \"login\": \"dolore3\",\n  \"name\": \"Nick Name3\",\n  \"email\": \"mail3@mail.ru\",\n  \"birthday\": \"1946-08-20\"\n}";

        MvcResult result3 = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson3))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result4 = mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isNoContent())
                .andReturn();

        MvcResult result5 = mockMvc.perform(put("/users/2/friends/3"))
                .andExpect(status().isNoContent())
                .andReturn();

        MvcResult result6 = mockMvc.perform(get("/users/1/friends/common/3"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result6.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        User[] users = objectMapper.readValue(responseBody, User[].class);

        Assertions.assertEquals(1, users.length);
        Assertions.assertEquals(2L, users[0].getId());
    }
}