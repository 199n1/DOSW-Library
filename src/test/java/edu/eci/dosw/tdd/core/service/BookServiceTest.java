package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.Validator.BookValidator;
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
        sampleBook = Book.builder().title("Clean Code").author("Robert C. Martin").build();
    }

    @Test
    void addBook_ShouldAddBookSuccessfully() {
        doNothing().when(bookValidator).validate(any(Book.class));
        Book added = bookService.addBook(sampleBook, 5);
        assertNotNull(added.getId());
        assertEquals(5, bookService.getAllBooks().get(added));
        verify(bookValidator, times(1)).validate(sampleBook);
    }

    @Test
    void addBook_ShouldThrowException_WhenValidatorFails() {
        doThrow(new IllegalArgumentException("El título y el autor del libro son obligatorios."))
                .when(bookValidator).validate(any());
        Book invalidBook = Book.builder().title("").build();
        assertThrows(IllegalArgumentException.class, () -> bookService.addBook(invalidBook, 1));
        assertFalse(bookService.getAllBooks().containsKey(invalidBook));
    }

    @Test
    void getAllBooks_ShouldReturnInventory() {
        doNothing().when(bookValidator).validate(any());
        bookService.addBook(sampleBook, 10);
        Map<Book, Integer> inventory = bookService.getAllBooks();
        assertEquals(1, inventory.size());
        assertEquals(10, inventory.get(sampleBook));
    }

    @Test
    void getBookById_ShouldReturnBook_WhenExists() {
        doNothing().when(bookValidator).validate(any());
        Book added = bookService.addBook(sampleBook, 1);
        Optional<Book> found = bookService.getBookById(added.getId());
        assertTrue(found.isPresent());
        assertEquals(added, found.get());
    }

    @Test
    void getBookById_ShouldReturnEmpty_WhenNotExists() {
        Optional<Book> found = bookService.getBookById("id-inexistente");
        assertFalse(found.isPresent());
    }

    @Test
    void isBookAvailable_ShouldReturnTrue_WhenQuantityGreaterThanZero() {
        doNothing().when(bookValidator).validate(any());
        bookService.addBook(sampleBook, 3);
        assertTrue(bookService.isBookAvailable(sampleBook));
    }

    @Test
    void isBookAvailable_ShouldReturnFalse_WhenQuantityIsZero() {
        doNothing().when(bookValidator).validate(any());
        bookService.addBook(sampleBook, 0);
        assertFalse(bookService.isBookAvailable(sampleBook));
    }

    @Test
    void updateBookAvailability_ShouldDecreaseQuantity() {
        doNothing().when(bookValidator).validate(any());
        bookService.addBook(sampleBook, 5);
        bookService.updateBookAvailability(sampleBook, -2);
        assertEquals(3, bookService.getAllBooks().get(sampleBook));
    }

    @Test
    void updateBookAvailability_ShouldIncreaseQuantity() {
        doNothing().when(bookValidator).validate(any());
        bookService.addBook(sampleBook, 2);
        bookService.updateBookAvailability(sampleBook, 3);
        assertEquals(5, bookService.getAllBooks().get(sampleBook));
    }
}