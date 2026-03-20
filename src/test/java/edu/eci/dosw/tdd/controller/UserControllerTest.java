package edu.eci.dosw.tdd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.dosw.tdd.controller.dto.UserDTO;
import edu.eci.dosw.tdd.controller.mapper.UserMapper;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private UserService userService;
    @MockitoBean private UserMapper userMapper;

    @Test
    void registerUser_ShouldReturnCreatedUser() throws Exception {
        UserDTO request = UserDTO.builder().name("Test User").build();
        User user = User.builder().id("1").name("Test User").build();
        UserDTO response = UserDTO.builder().id("1").name("Test User").build();

        when(userMapper.toEntity(any())).thenReturn(user);
        when(userService.registerUser(any())).thenReturn(user);
        when(userMapper.toDto(any())).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void getAllUsers_ShouldReturnList() throws Exception {
        User user = User.builder().id("1").name("Test User").build();
        UserDTO dto = UserDTO.builder().id("1").name("Test User").build();

        when(userService.getAllUsers()).thenReturn(Collections.singletonList(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"));
    }

    @Test
    void getUserById_ShouldReturnUser_WhenExists() throws Exception {
        User user = User.builder().id("1").name("Test User").build();
        UserDTO dto = UserDTO.builder().id("1").name("Test User").build();

        when(userService.getUserById("1")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void getUserById_ShouldReturn400_WhenNotExists() throws Exception {
        when(userService.getUserById("99")).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isBadRequest());
    }
}