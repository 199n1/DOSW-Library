package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.validator.BookValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookValidator bookValidator;

    @InjectMocks
    private BookService bookService;

    private Book sampleBook;

    @BeforeEach
    void setUp() {
        sampleBook = Book.builder()
                .title("Test Book")
                .author("Test Author")
                .build();
    }

    @Test
    void addBook_ShouldAddBookSuccessfully() {
        // Arrange
        doNothing().when(bookValidator).validate(any(Book.class));

        // Act
        Book addedBook = bookService.addBook(sampleBook, 5);

        // Assert
        assertNotNull(addedBook.getId());
        assertEquals("Test Book", addedBook.getTitle());
        verify(bookValidator, times(1)).validate(sampleBook);
        assertEquals(5, bookService.getAllBooks().get(addedBook));
    }

    @Test
    void addBook_ShouldThrowException_WhenValidatorFails() {
        // Arrange
        doThrow(new IllegalArgumentException("El título y el autor del libro son obligatorios"))
                .when(bookValidator).validate(any());

        Book invalidBook = Book.builder().title("").build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> bookService.addBook(invalidBook, 1));
        verify(bookValidator, times(1)).validate(invalidBook);
        assertFalse(bookService.getAllBooks().containsKey(invalidBook));
    }

    @Test
    void getAllBooks_ShouldReturnInventory() {
        // Arrange
        doNothing().when(bookValidator).validate(any());
        bookService.addBook(sampleBook, 10);

        // Act
        Map<Book, Integer> inventory = bookService.getAllBooks();

        // Assert
        assertEquals(1, inventory.size());
        assertEquals(10, inventory.get(sampleBook));
    }

    @Test
    void getBookById_ShouldReturnBook_WhenExists() {
        // Arrange
        doNothing().when(bookValidator).validate(any());
        Book addedBook = bookService.addBook(sampleBook, 1);
        String bookId = addedBook.getId();

        // Act
        Optional<Book> foundBook = bookService.getBookById(bookId);

        // Assert
        assertTrue(foundBook.isPresent());
        assertEquals(addedBook, foundBook.get());
    }

    @Test
    void isBookAvailable_ShouldReturnTrue_WhenQuantityIsGreaterThanZero() {
        // Arrange
        doNothing().when(bookValidator).validate(any());
        bookService.addBook(sampleBook, 5);

        // Act & Assert
        assertTrue(bookService.isBookAvailable(sampleBook));
    }

    @Test
    void isBookAvailable_ShouldReturnFalse_WhenQuantityIsZero() {
        // Arrange
        doNothing().when(bookValidator).validate(any());
        bookService.addBook(sampleBook, 0);

        // Act & Assert
        assertFalse(bookService.isBookAvailable(sampleBook));
    }

    @Test
    void updateBookAvailability_ShouldChangeQuantity() {
        // Arrange
        doNothing().when(bookValidator).validate(any());
        bookService.addBook(sampleBook, 5);

        // Act
        bookService.updateBookAvailability(sampleBook, -2);

        // Assert
        assertEquals(3, bookService.getAllBooks().get(sampleBook));
    }
}
