package edu.eci.dosw.tdd.core.Validator;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoanValidatorTest {

    private final LoanValidator validator = new LoanValidator();

    @Test
    void validateLoanCreation_ShouldDoNothing_WhenBothAreValid() {
        User user = User.builder().id("u1").name("Test User").build();
        Book book = Book.builder().id("b1").title("Clean Code").build();
        assertDoesNotThrow(() -> validator.validateLoanCreation(user, book));
    }

    @Test
    void validateLoanCreation_ShouldThrowException_WhenUserIsNull() {
        Book book = Book.builder().id("b1").title("Clean Code").build();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validateLoanCreation(null, book));
        assertEquals("El usuario es requerido para realizar el préstamo.", ex.getMessage());
    }

    @Test
    void validateLoanCreation_ShouldThrowException_WhenBookIsNull() {
        User user = User.builder().id("u1").name("Test User").build();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validateLoanCreation(user, null));
        assertEquals("El libro es requerido para realizar el préstamo.", ex.getMessage());
    }
}