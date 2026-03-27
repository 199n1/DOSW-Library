package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.Validator.BookValidator;
import edu.eci.dosw.tdd.persistence.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.mapper.BookPersistenceMapper;
import edu.eci.dosw.tdd.persistence.repository.BookRepository;
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
class BookServiceTest {

    @Mock private BookValidator bookValidator;
    @Mock private BookRepository bookRepository;
    @Mock private BookPersistenceMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    private Book sampleBook;
    private BookEntity sampleEntity;

    @BeforeEach
    void setUp() {
        sampleBook = Book.builder()
                .id("b1")
                .title("Clean Code")
                .author("Robert C. Martin")
                .totalStock(5)
                .availableStock(5)
                .build();

        sampleEntity = BookEntity.builder()
                .id("b1")
                .title("Clean Code")
                .author("Robert C. Martin")
                .totalStock(5)
                .availableStock(5)
                .build();
    }

    @Test
    void addBook_ShouldAddBookSuccessfully() {
        doNothing().when(bookValidator).validate(any());
        when(bookMapper.toEntity(any())).thenReturn(sampleEntity);
        when(bookRepository.save(any())).thenReturn(sampleEntity);
        when(bookMapper.toDomain(any())).thenReturn(sampleBook);

        Book result = bookService.addBook(sampleBook, 5);

        assertNotNull(result);
        assertEquals("Clean Code", result.getTitle());
        assertEquals(5, result.getTotalStock());
        verify(bookRepository, times(1)).save(any());
    }

    @Test
    void addBook_ShouldThrowException_WhenQuantityIsZero() {
        doNothing().when(bookValidator).validate(any());

        assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook(sampleBook, 0));

        verify(bookRepository, never()).save(any());
    }

    @Test
    void addBook_ShouldThrowException_WhenValidatorFails() {
        doThrow(new IllegalArgumentException("El título y el autor son obligatorios."))
                .when(bookValidator).validate(any());

        assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook(Book.builder().title("").build(), 5));

        verify(bookRepository, never()).save(any());
    }

    @Test
    void getAllBooks_ShouldReturnList() {
        when(bookRepository.findAll()).thenReturn(List.of(sampleEntity));
        when(bookMapper.toDomain(sampleEntity)).thenReturn(sampleBook);

        List<Book> result = bookService.getAllBooks();

        assertEquals(1, result.size());
        assertEquals("Clean Code", result.get(0).getTitle());
    }

    @Test
    void getBookById_ShouldReturnBook_WhenExists() {
        when(bookRepository.findById("b1")).thenReturn(Optional.of(sampleEntity));
        when(bookMapper.toDomain(sampleEntity)).thenReturn(sampleBook);

        Optional<Book> result = bookService.getBookById("b1");

        assertTrue(result.isPresent());
        assertEquals("b1", result.get().getId());
    }

    @Test
    void getBookById_ShouldReturnEmpty_WhenNotExists() {
        when(bookRepository.findById("id-inexistente")).thenReturn(Optional.empty());

        Optional<Book> result = bookService.getBookById("id-inexistente");

        assertFalse(result.isPresent());
    }

    @Test
    void isBookAvailable_ShouldReturnTrue_WhenStockGreaterThanZero() {
        when(bookRepository.findById("b1")).thenReturn(Optional.of(sampleEntity));

        assertTrue(bookService.isBookAvailable(sampleBook));
    }

    @Test
    void isBookAvailable_ShouldReturnFalse_WhenStockIsZero() {
        sampleEntity.setAvailableStock(0);
        when(bookRepository.findById("b1")).thenReturn(Optional.of(sampleEntity));

        assertFalse(bookService.isBookAvailable(sampleBook));
    }

    @Test
    void updateBookAvailability_ShouldDecreaseStock() {
        when(bookRepository.findById("b1")).thenReturn(Optional.of(sampleEntity));
        when(bookRepository.save(any())).thenReturn(sampleEntity);

        bookService.updateBookAvailability(sampleBook, -2);

        verify(bookRepository, times(1)).save(any());
        assertEquals(3, sampleEntity.getAvailableStock());
    }

    @Test
    void updateBookAvailability_ShouldThrow_WhenStockGoesNegative() {
        sampleEntity.setAvailableStock(1);
        when(bookRepository.findById("b1")).thenReturn(Optional.of(sampleEntity));

        assertThrows(IllegalArgumentException.class,
                () -> bookService.updateBookAvailability(sampleBook, -5));
    }
}