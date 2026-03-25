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
        assertEquals("Se necesita el usuario para realizar el prestamo", ex.getMessage());
    }

    @Test
    void validateLoanCreation_ShouldThrowException_WhenBookIsNull() {
        User user = User.builder().id("u1").name("Test User").build();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validateLoanCreation(user, null));
        assertEquals("Se necesita el libro para realizar el prestamo", ex.getMessage());
    }
}