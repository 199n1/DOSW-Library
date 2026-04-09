package edu.eci.dosw.tdd.core.Validator;

import edu.eci.dosw.tdd.core.model.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {

    private final UserValidator validator = new UserValidator();

    @Test
    void validate_ShouldDoNothing_WhenUserIsValid() {
        User user = User.builder()
                .name("Test User")
                .username("testuser")
                .password("pass123")
                .build();
        assertDoesNotThrow(() -> validator.validate(user));
    }

    @Test
    void validate_ShouldThrowException_WhenUserIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validate(null));
        assertEquals("El usuario debe ser diferente de null", ex.getMessage());
    }

    @Test
    void validate_ShouldThrowException_WhenNameIsEmpty() {
        User user = User.builder().name("").username("testuser").password("pass").build();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validate(user));
        assertEquals("El nombre del usuario no puede estar vacio.", ex.getMessage());
    }

    @Test
    void validate_ShouldThrowException_WhenNameIsBlank() {
        User user = User.builder().name("   ").username("testuser").password("pass").build();
        assertThrows(IllegalArgumentException.class, () -> validator.validate(user));
    }

    @Test
    void validate_ShouldThrowException_WhenUsernameIsEmpty() {
        User user = User.builder().name("Test User").username("").password("pass").build();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validate(user));
        assertEquals("El username no puede estar vacio.", ex.getMessage());
    }

    @Test
    void validate_ShouldThrowException_WhenPasswordIsEmpty() {
        User user = User.builder().name("Test User").username("testuser").password("").build();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validate(user));
        assertEquals("La contrasena no puede estar vacia.", ex.getMessage());
    }

    @Test
    void validate_ShouldThrowException_WhenRoleIsInvalid() {
        User user = User.builder()
                .name("Test User")
                .username("testuser")
                .password("pass")
                .role("ADMIN")
                .build();
        assertThrows(IllegalArgumentException.class, () -> validator.validate(user));
    }
}