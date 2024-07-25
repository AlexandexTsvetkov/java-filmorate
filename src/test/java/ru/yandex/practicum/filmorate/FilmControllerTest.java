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
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.servise.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        FilmStorage filmStorage = new InMemoryFilmStorage(userStorage);
        FilmService filmService = new FilmService(filmStorage);
        FilmController filmController = new FilmController(filmService);
        mockMvc = MockMvcBuilders.standaloneSetup(filmController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testCreateFilm() throws Exception {
        String filmJson = "{\"name\":\"nisi eiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\",\"duration\":100}";

        MvcResult result = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        Film film = objectMapper.readValue(responseBody, Film.class);

        Assertions.assertEquals(1L, film.getId());
        Assertions.assertEquals("nisi eiusmod", film.getName());
        Assertions.assertEquals("adipisicing", film.getDescription());
        Assertions.assertEquals(100, film.getDuration());
        Assertions.assertEquals(LocalDate.of(1967, 3, 25), film.getReleaseDate());
    }

    @Test
    void testValidateReleaseDate() throws Exception {
        String filmJson = "{\"name\":\"nisi eiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1890-03-25\",\"duration\":100}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testValidateName() throws Exception {
        String filmJson = "{\"name\":\"\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\",\"duration\":100}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testValidateDuration() throws Exception {
        String filmJson = "{\"name\":\"nisi eiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\",\"duration\":-100}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testValidateDescription() throws Exception {

        String wrongDescription = "a".repeat(201);

        String filmJson = "{\"name\":\"nisi eiusmod\",\"description\":" + wrongDescription + ",\"releaseDate\":\"1967-03-25\",\"duration\":100}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testNotFound() throws Exception {
        String filmJson = "{\"id\":1000,\"name\":\"nisi eiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\",\"duration\":100}";

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEmptyRequest() throws Exception {

        mockMvc.perform(post("/films"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGet() throws Exception {
        String filmJson = "{\"name\":\"nisi eiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\",\"duration\":100}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        Film[] films = objectMapper.readValue(responseBody, Film[].class);

        Assertions.assertEquals(1, films.length);
    }

    @Test
    void testAddLike() throws Exception {

        String userJson = "{\n  \"login\": \"dolore\",\n  \"name\": \"Nick Name\",\n  \"email\": \"mail@mail.ru\",\n  \"birthday\": \"1946-08-20\"\n}";

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        User user = objectMapper.readValue(userJson, User.class);

        userStorage.create(user);

        String filmJson = "{\"name\":\"nisi eiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\",\"duration\":100}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isOk());

        mockMvc.perform(put("/films/1/like/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isNoContent());

        MvcResult result = mockMvc.perform(get("/films/1/likes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Long[] likes = objectMapper.readValue(responseBody, Long[].class);

        Assertions.assertEquals(1, likes.length);
    }

    @Test
    void testDeleteLike() throws Exception {

        String userJson = "{\n  \"login\": \"dolore\",\n  \"name\": \"Nick Name\",\n  \"email\": \"mail@mail.ru\",\n  \"birthday\": \"1946-08-20\"\n}";

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        User user = objectMapper.readValue(userJson, User.class);

        userStorage.create(user);

        String filmJson = "{\"name\":\"nisi eiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\",\"duration\":100}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isOk());

        mockMvc.perform(put("/films/1/like/1"))
                .andExpect(status().isNoContent());

        MvcResult result = mockMvc.perform(get("/films/1/likes"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Long[] likes = objectMapper.readValue(responseBody, Long[].class);

        Assertions.assertEquals(1, likes.length);

        mockMvc.perform(delete("/films/1/like/1"))
                .andExpect(status().isNoContent());

        MvcResult result2 = mockMvc.perform(get("/films/1/likes"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody2 = result2.getResponse().getContentAsString();
        Long[] likes2 = objectMapper.readValue(responseBody2, Long[].class);

        Assertions.assertEquals(0, likes2.length);
    }

    @Test
    void testPopular() throws Exception {

        String userJson = "{\n  \"login\": \"dolore\",\n  \"name\": \"Nick Name\",\n  \"email\": \"mail@mail.ru\",\n  \"birthday\": \"1946-08-20\"\n}";

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        User user = objectMapper.readValue(userJson, User.class);

        userStorage.create(user);

        String filmJson = "{\"name\":\"nisi eiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\",\"duration\":100}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isOk());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isOk());

        mockMvc.perform(put("/films/1/like/1"))
                .andExpect(status().isNoContent());

        MvcResult result = mockMvc.perform(get("/films/popular?count=10"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Film[] films = objectMapper.readValue(responseBody, Film[].class);

        Assertions.assertEquals(2, films.length);
        Assertions.assertEquals(1L, films[0].getId());
    }
}