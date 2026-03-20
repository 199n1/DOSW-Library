package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvaibleException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.validator.LoanValidator;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private BookService bookService;

    @Mock
    private LoanValidator loanValidator;

    @InjectMocks
    private LoanService loanService;

    private User sampleUser;
    private Book sampleBook;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder().id("user-1").name("Test User").build();
        sampleBook = Book.builder().id("book-1").title("Test Book").build();
    }

    @Test
    void createLoan_ShouldCreateLoanSuccessfully() {
        // Arrange
        when(userService.getUserById("user-1")).thenReturn(Optional.of(sampleUser));
        when(bookService.getBookById("book-1")).thenReturn(Optional.of(sampleBook));
        when(bookService.isBookAvailable(sampleBook)).thenReturn(true);
        doNothing().when(loanValidator).validateLoanCreation(sampleUser, sampleBook);

        // Act
        Loan loan = loanService.createLoan("user-1", "book-1");

        // Assert
        assertNotNull(loan);
        assertEquals(sampleUser, loan.getUser());
        assertEquals(sampleBook, loan.getBook());
        assertEquals(LoanStatus.ACTIVE, loan.getStatus());
        verify(bookService, times(1)).updateBookAvailability(sampleBook, -1);
    }

    @Test
    void createLoan_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        // Arrange
        when(userService.getUserById("user-1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> loanService.createLoan("user-1", "book-1"));
    }

    @Test
    void createLoan_ShouldThrowBookNotAvaibleException_WhenBookIsNotAvailable() {
        // Arrange
        when(userService.getUserById("user-1")).thenReturn(Optional.of(sampleUser));
        when(bookService.getBookById("book-1")).thenReturn(Optional.of(sampleBook));
        when(bookService.isBookAvailable(sampleBook)).thenReturn(false);

        // Act & Assert
        assertThrows(BookNotAvaibleException.class, () -> loanService.createLoan("user-1", "book-1"));
    }

    @Test
    void createLoan_ShouldThrowLoanLimitExceededException_WhenUserHasThreeActiveLoans() {
        // Arrange
        when(userService.getUserById("user-1")).thenReturn(Optional.of(sampleUser));
        when(bookService.getBookById(anyString())).thenReturn(Optional.of(sampleBook));
        when(bookService.isBookAvailable(any())).thenReturn(true);

        loanService.createLoan("user-1", "book-1");
        loanService.createLoan("user-1", "book-2");
        loanService.createLoan("user-1", "book-3");

        // Act & Assert
        assertThrows(LoanLimitExceededException.class, () -> loanService.createLoan("user-1", "book-4"));
    }

    @Test
    void returnLoan_ShouldReturnBookSuccessfully() {
        // Arrange
        when(userService.getUserById("user-1")).thenReturn(Optional.of(sampleUser));
        when(bookService.getBookById("book-1")).thenReturn(Optional.of(sampleBook));
        when(bookService.isBookAvailable(sampleBook)).thenReturn(true);
        loanService.createLoan("user-1", "book-1");

        // Act
        Loan returnedLoan = loanService.returnLoan("user-1", "book-1");

        // Assert
        assertEquals(LoanStatus.RETURNED, returnedLoan.getStatus());
        assertNotNull(returnedLoan.getReturnDate());
        verify(bookService, times(1)).updateBookAvailability(sampleBook, 1);
    }

    @Test
    void returnLoan_ShouldThrowException_WhenNoActiveLoanFound() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> loanService.returnLoan("user-1", "book-1"));
    }

    @Test
    void getAllLoans_ShouldReturnAllLoans() {
        // Arrange
        when(userService.getUserById(anyString())).thenReturn(Optional.of(sampleUser));
        when(bookService.getBookById(anyString())).thenReturn(Optional.of(sampleBook));
        when(bookService.isBookAvailable(any())).thenReturn(true);
        loanService.createLoan("user-1", "book-1");

        // Act
        List<Loan> allLoans = loanService.getAllLoans();

        // Assert
        assertEquals(1, allLoans.size());
    }
}
