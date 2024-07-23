package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.advice.GlobalExceptionHandler;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.servise.FilmService;
import ru.yandex.practicum.filmorate.servise.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        UserStorage userStorage = new InMemoryUserStorage();
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
}