// LoanServiceTest.java
package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;  // ← corregido
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.Validator.LoanValidator;  // ← corregido
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

    @Mock private UserService userService;
    @Mock private BookService bookService;
    @Mock private LoanValidator loanValidator;

    @InjectMocks
    private LoanService loanService;

    private User sampleUser;
    private Book sampleBook;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder().id("user-1").name("Test User").build();
        sampleBook = Book.builder().id("book-1").title("Clean Code").author("Uncle Bob").build();
    }

    @Test
    void createLoan_ShouldCreateLoanSuccessfully() {
        when(userService.getUserById("user-1")).thenReturn(Optional.of(sampleUser));
        when(bookService.getBookById("book-1")).thenReturn(Optional.of(sampleBook));
        when(bookService.isBookAvailable(sampleBook)).thenReturn(true);
        doNothing().when(loanValidator).validateLoanCreation(sampleUser, sampleBook);

        Loan loan = loanService.createLoan("user-1", "book-1");

        assertNotNull(loan);
        assertEquals(sampleUser, loan.getUser());
        assertEquals(sampleBook, loan.getBook());
        assertEquals(LoanStatus.ACTIVE, loan.getStatus());
        assertNotNull(loan.getLoanDate());
        verify(bookService, times(1)).updateBookAvailability(sampleBook, -1);
    }

    @Test
    void createLoan_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(userService.getUserById("user-1")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> loanService.createLoan("user-1", "book-1"));
    }

    @Test
    void createLoan_ShouldThrowIllegalArgument_WhenBookDoesNotExist() {
        when(userService.getUserById("user-1")).thenReturn(Optional.of(sampleUser));
        when(bookService.getBookById("book-1")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> loanService.createLoan("user-1", "book-1"));
    }

    @Test
    void createLoan_ShouldThrowBookNotAvailableException_WhenBookIsNotAvailable() {
        when(userService.getUserById("user-1")).thenReturn(Optional.of(sampleUser));
        when(bookService.getBookById("book-1")).thenReturn(Optional.of(sampleBook));
        when(bookService.isBookAvailable(sampleBook)).thenReturn(false);
        assertThrows(BookNotAvailableException.class,
                () -> loanService.createLoan("user-1", "book-1"));
    }

    @Test
    void createLoan_ShouldThrowLoanLimitExceededException_WhenUserHasThreeActiveLoans() {
        when(userService.getUserById("user-1")).thenReturn(Optional.of(sampleUser));
        when(bookService.getBookById(anyString())).thenReturn(Optional.of(sampleBook));
        when(bookService.isBookAvailable(any())).thenReturn(true);

        loanService.createLoan("user-1", "book-1");
        loanService.createLoan("user-1", "book-2");
        loanService.createLoan("user-1", "book-3");

        assertThrows(LoanLimitExceededException.class,
                () -> loanService.createLoan("user-1", "book-4"));
    }

    @Test
    void returnLoan_ShouldReturnBookSuccessfully() {
        when(userService.getUserById("user-1")).thenReturn(Optional.of(sampleUser));
        when(bookService.getBookById("book-1")).thenReturn(Optional.of(sampleBook));
        when(bookService.isBookAvailable(sampleBook)).thenReturn(true);
        loanService.createLoan("user-1", "book-1");

        Loan returned = loanService.returnLoan("user-1", "book-1");

        assertEquals(LoanStatus.RETURNED, returned.getStatus());
        assertNotNull(returned.getReturnDate());
        verify(bookService, times(1)).updateBookAvailability(sampleBook, 1);
    }

    @Test
    void returnLoan_ShouldThrowException_WhenNoActiveLoanFound() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.returnLoan("user-1", "book-1"));
    }

    @Test
    void getAllLoans_ShouldReturnAllLoans() {
        when(userService.getUserById(anyString())).thenReturn(Optional.of(sampleUser));
        when(bookService.getBookById(anyString())).thenReturn(Optional.of(sampleBook));
        when(bookService.isBookAvailable(any())).thenReturn(true);
        loanService.createLoan("user-1", "book-1");

        List<Loan> all = loanService.getAllLoans();
        assertEquals(1, all.size());
    }
}