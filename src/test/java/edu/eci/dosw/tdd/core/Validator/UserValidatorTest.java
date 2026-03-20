package edu.eci.dosw.tdd.core.Validator;

import edu.eci.dosw.tdd.core.model.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {

    private final UserValidator validator = new UserValidator();

    @Test
    void validate_ShouldDoNothing_WhenUserIsValid() {
        User user = User.builder().name("Test User").build();
        assertDoesNotThrow(() -> validator.validate(user));
    }

    @Test
    void validate_ShouldThrowException_WhenUserIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validate(null));
        assertEquals("El usuario no puede ser nulo.", ex.getMessage());
    }

    @Test
    void validate_ShouldThrowException_WhenNameIsEmpty() {
        User user = User.builder().name("").build();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validate(user));
        assertEquals("El nombre del usuario no puede estar vacío.", ex.getMessage());
    }

    @Test
    void validate_ShouldThrowException_WhenNameIsBlank() {
        User user = User.builder().name("   ").build();
        assertThrows(IllegalArgumentException.class, () -> validator.validate(user));
    }
}