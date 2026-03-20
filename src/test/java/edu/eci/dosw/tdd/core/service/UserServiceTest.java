// UserServiceTest.java
package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.Validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder().name("Test User").build();
    }

    @Test
    void registerUser_ShouldRegisterSuccessfully() {
        doNothing().when(userValidator).validate(any(User.class));
        User registered = userService.registerUser(sampleUser);
        assertNotNull(registered.getId());
        assertEquals("Test User", registered.getName());
        assertTrue(userService.getAllUsers().contains(registered));
        verify(userValidator, times(1)).validate(sampleUser);
    }

    @Test
    void registerUser_ShouldThrowException_WhenValidatorFails() {
        doThrow(new IllegalArgumentException("El nombre del usuario no puede estar vacío."))
                .when(userValidator).validate(any());
        User invalidUser = User.builder().name("").build();
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(invalidUser));
        assertFalse(userService.getAllUsers().contains(invalidUser));
    }

    @Test
    void getAllUsers_ShouldReturnList() {
        doNothing().when(userValidator).validate(any());
        userService.registerUser(sampleUser);
        List<User> users = userService.getAllUsers();
        assertEquals(1, users.size());
    }

    @Test
    void getUserById_ShouldReturnUser_WhenExists() {
        doNothing().when(userValidator).validate(any());
        User registered = userService.registerUser(sampleUser);
        Optional<User> found = userService.getUserById(registered.getId());
        assertTrue(found.isPresent());
        assertEquals(registered, found.get());
    }

    @Test
    void getUserById_ShouldReturnEmpty_WhenNotExists() {
        Optional<User> found = userService.getUserById("id-inexistente");
        assertFalse(found.isPresent());
    }
}