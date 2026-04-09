package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.Validator.LoanValidator;
import edu.eci.dosw.tdd.persistence.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.mapper.BookPersistenceMapper;
import edu.eci.dosw.tdd.persistence.mapper.LoanPersistenceMapper;
import edu.eci.dosw.tdd.persistence.mapper.UserPersistenceMapper;
import edu.eci.dosw.tdd.persistence.repository.BookRepository;
import edu.eci.dosw.tdd.persistence.repository.LoanRepository;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private BookRepository bookRepository;
    @Mock private LoanRepository loanRepository;
    @Mock private LoanValidator loanValidator;
    @Mock private LoanPersistenceMapper loanMapper;
    @Mock private UserPersistenceMapper userMapper;
    @Mock private BookPersistenceMapper bookMapper;

    @InjectMocks
    private LoanService loanService;

    private UserEntity userEntity;
    private BookEntity bookEntity;
    private User sampleUser;
    private Book sampleBook;
    private LoanEntity loanEntity;
    private Loan sampleLoan;

    @BeforeEach
    void setUp() {
        userEntity = UserEntity.builder()
                .id("user-1").name("Test User").username("testuser")
                .password("pass").role(UserEntity.Role.USER).build();

        // FIX: availableStock < totalStock para que returnLoan no lance excepcion
        bookEntity = BookEntity.builder()
                .id("book-1").title("Clean Code").author("Uncle Bob")
                .totalStock(5).availableStock(3).build();

        sampleUser = User.builder().id("user-1").name("Test User").build();
        sampleBook = Book.builder().id("book-1").title("Clean Code").author("Uncle Bob").build();

        loanEntity = LoanEntity.builder()
                .id("loan-1").user(userEntity).book(bookEntity)
                .loanDate(LocalDate.now()).status(LoanEntity.LoanStatus.ACTIVE).build();

        sampleLoan = Loan.builder()
                .id("loan-1").user(sampleUser).book(sampleBook)
                .loanDate(LocalDate.now()).status(LoanStatus.ACTIVE).build();
    }

    @Test
    void createLoan_ShouldCreateLoanSuccessfully() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(userEntity));
        when(bookRepository.findById("book-1")).thenReturn(Optional.of(bookEntity));
        when(userMapper.toDomain(userEntity)).thenReturn(sampleUser);
        when(bookMapper.toDomain(bookEntity)).thenReturn(sampleBook);
        doNothing().when(loanValidator).validateLoanCreation(sampleUser, sampleBook);
        when(loanRepository.countByUserIdAndStatus("user-1", LoanEntity.LoanStatus.ACTIVE)).thenReturn(0L);
        when(bookRepository.save(any())).thenReturn(bookEntity);
        when(loanRepository.save(any())).thenReturn(loanEntity);
        when(loanMapper.toDomain(loanEntity)).thenReturn(sampleLoan);

        Loan result = loanService.createLoan("user-1", "book-1");

        assertNotNull(result);
        assertEquals("loan-1", result.getId());
        assertEquals(LoanStatus.ACTIVE, result.getStatus());
        verify(bookRepository, times(1)).save(any());
        verify(loanRepository, times(1)).save(any());
    }

    @Test
    void createLoan_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById("user-1")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> loanService.createLoan("user-1", "book-1"));
    }

    @Test
    void createLoan_ShouldThrowIllegalArgument_WhenBookDoesNotExist() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(userEntity));
        when(bookRepository.findById("book-1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> loanService.createLoan("user-1", "book-1"));
    }

    @Test
    void createLoan_ShouldThrowBookNotAvailableException_WhenBookHasNoStock() {
        bookEntity.setAvailableStock(0);
        when(userRepository.findById("user-1")).thenReturn(Optional.of(userEntity));
        when(bookRepository.findById("book-1")).thenReturn(Optional.of(bookEntity));
        when(userMapper.toDomain(userEntity)).thenReturn(sampleUser);
        when(bookMapper.toDomain(bookEntity)).thenReturn(sampleBook);
        doNothing().when(loanValidator).validateLoanCreation(any(), any());

        assertThrows(BookNotAvailableException.class,
                () -> loanService.createLoan("user-1", "book-1"));
    }

    @Test
    void createLoan_ShouldThrowLoanLimitExceededException_WhenUserHasThreeActiveLoans() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(userEntity));
        when(bookRepository.findById("book-1")).thenReturn(Optional.of(bookEntity));
        when(userMapper.toDomain(userEntity)).thenReturn(sampleUser);
        when(bookMapper.toDomain(bookEntity)).thenReturn(sampleBook);
        doNothing().when(loanValidator).validateLoanCreation(any(), any());
        when(loanRepository.countByUserIdAndStatus("user-1", LoanEntity.LoanStatus.ACTIVE)).thenReturn(3L);

        assertThrows(LoanLimitExceededException.class,
                () -> loanService.createLoan("user-1", "book-1"));
    }

    @Test
    void returnLoan_ShouldReturnBookSuccessfully() {
        // FIX: availableStock(3) < totalStock(5) -> la devolución es válida
        LoanEntity returnedEntity = LoanEntity.builder()
                .id("loan-1").user(userEntity).book(bookEntity)
                .loanDate(LocalDate.now())
                .status(LoanEntity.LoanStatus.RETURNED)
                .returnDate(LocalDate.now()).build();

        Loan returnedLoan = Loan.builder()
                .id("loan-1").user(sampleUser).book(sampleBook)
                .loanDate(LocalDate.now())
                .status(LoanStatus.RETURNED)
                .returnDate(LocalDate.now()).build();

        when(loanRepository.findByUserIdAndBookIdAndStatus(
                "user-1", "book-1", LoanEntity.LoanStatus.ACTIVE))
                .thenReturn(Optional.of(loanEntity));
        when(bookRepository.save(any())).thenReturn(bookEntity);
        when(loanRepository.save(any())).thenReturn(returnedEntity);
        when(loanMapper.toDomain(returnedEntity)).thenReturn(returnedLoan);

        Loan result = loanService.returnLoan("user-1", "book-1");

        assertEquals(LoanStatus.RETURNED, result.getStatus());
        assertNotNull(result.getReturnDate());
        verify(loanRepository, times(1)).save(any());
    }

    @Test
    void returnLoan_ShouldThrowException_WhenNoActiveLoanFound() {
        when(loanRepository.findByUserIdAndBookIdAndStatus(
                "user-1", "book-1", LoanEntity.LoanStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> loanService.returnLoan("user-1", "book-1"));
    }

    @Test
    void getAllLoans_ShouldReturnAllLoans() {
        when(loanRepository.findAll()).thenReturn(List.of(loanEntity));
        when(loanMapper.toDomain(loanEntity)).thenReturn(sampleLoan);

        List<Loan> result = loanService.getAllLoans();

        assertEquals(1, result.size());
        assertEquals("loan-1", result.get(0).getId());
    }

    @Test
    void getLoansByUsername_ShouldReturnLoans_WhenUserExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(userEntity));
        when(loanRepository.findByUser(userEntity)).thenReturn(List.of(loanEntity));
        when(loanMapper.toDomain(loanEntity)).thenReturn(sampleLoan);

        List<Loan> result = loanService.getLoansByUsername("testuser");

        assertEquals(1, result.size());
    }

    @Test
    void getLoansByUsername_ShouldThrowUserNotFoundException_WhenUserNotFound() {
        when(userRepository.findByUsername("noexiste")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> loanService.getLoansByUsername("noexiste"));
    }
}