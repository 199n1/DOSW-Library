package edu.eci.dosw.tdd.core.validator;

import edu.eci.dosw.tdd.core.model.Book;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookValidatorTest {

    private final edu.eci.dosw.tdd.core.validator.BookValidator validator = new edu.eci.dosw.tdd.core.validator.BookValidator();

    @Test
    void validate_ShouldDoNothing_WhenBookIsValid() {
        Book validBook = Book.builder().title("Clean Code").author("Robert C. Martin").build();
        assertDoesNotThrow(() -> validator.validate(validBook));
    }

    @Test
    void validate_ShouldThrowException_WhenBookIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(null));
        assertEquals("El libro no puede ser nulo", exception.getMessage());
    }

    @Test
    void validate_ShouldThrowException_WhenTitleOrAuthorIsNullOrEmpty() {
        Book noTitle = Book.builder().author("Author").build();
        Book noAuthor = Book.builder().title("Title").build();
        Book emptyTitle = Book.builder().title("").author("Author").build();

        assertThrows(IllegalArgumentException.class, () -> validator.validate(noTitle));
        assertThrows(IllegalArgumentException.class, () -> validator.validate(noAuthor));
        assertThrows(IllegalArgumentException.class, () -> validator.validate(emptyTitle));
    }
}
