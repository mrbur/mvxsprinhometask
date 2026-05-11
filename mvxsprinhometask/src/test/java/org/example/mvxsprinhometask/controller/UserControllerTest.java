package org.example.mvxsprinhometask.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mvxsprinhometask.dto.User;

import org.example.mvxsprinhometask.repository.UserRepository;
import org.example.mvxsprinhometask.servise.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.data.jpa.test.autoconfigure.AutoConfigureDataJpa;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(UserService.class)
@AutoConfigureDataJpa
@ActiveProfiles("test")
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Test
    //сквозной тест
    //создаём юзера -> проверяем что создался -> получаем юзера -> удаляем юзера -> проверяем что удалили
    public void createGetRemoveSuccessUser() throws Exception {
        User inputUser = new User("new user", "user@mail.com");

        long countBefore = userRepository.count();

        //создаём, сверяем
        var result = mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("new user"))
                .andExpect(jsonPath("$.email").value("user@mail.com"))
                .andReturn();

        //проверяем появление новой записи
        Assertions.assertEquals(countBefore + 1, userRepository.count());

        String jsonResponse = result.getResponse().getContentAsString();
        User createdUser = objectMapper.readValue(jsonResponse, User.class);

        //получаем пользователя, сверяем
        mockMvc.perform(get("/api/v1/user/" + createdUser.getId() + "/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("new user"))
                .andExpect(jsonPath("$.email").value("user@mail.com"));

        //удаляем
        //получаем пользователя, сверяем
        mockMvc.perform(delete("/api/v1/user/" + createdUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isOk());

        //проверяем что удалили
        Assertions.assertEquals(countBefore, userRepository.count());
    }

    @Test
    public void createUserWithError() throws Exception {

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new User(null, "user@mail.com"))))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new User("user name", null))))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new User("user name", "user@mail.com"))))
                .andExpect(status().isInternalServerError());
    }

}