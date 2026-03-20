
package edu.eci.dosw.tdd.core.Validator;

import edu.eci.dosw.tdd.core.model.Book;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookValidatorTest {

    private final BookValidator validator = new BookValidator();

    @Test
    void validate_ShouldDoNothing_WhenBookIsValid() {
        Book validBook = Book.builder().title("Clean Code").author("Robert C. Martin").build();
        assertDoesNotThrow(() -> validator.validate(validBook));
    }

    @Test
    void validate_ShouldThrowException_WhenBookIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validate(null));
        assertEquals("El libro tiene que ser diferente de nulo", ex.getMessage());
    }

    @Test
    void validate_ShouldThrowException_WhenTitleIsEmpty() {
        Book book = Book.builder().title("").author("Author").build();
        assertThrows(IllegalArgumentException.class, () -> validator.validate(book));
    }

    @Test
    void validate_ShouldThrowException_WhenAuthorIsNull() {
        Book book = Book.builder().title("Title").build();
        assertThrows(IllegalArgumentException.class, () -> validator.validate(book));
    }
}