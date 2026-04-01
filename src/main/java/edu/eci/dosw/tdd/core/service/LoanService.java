package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.util.DateUtil;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final LoanValidator loanValidator;
    private final LoanPersistenceMapper loanMapper;
    private final UserPersistenceMapper userMapper;
    private final BookPersistenceMapper bookMapper;

    public Loan createLoan(String userId, String bookId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "No se encuentra ningún usuario registrado con el ID: " + userId));

        BookEntity bookEntity = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encuentra ningún libro registrado con el ID: " + bookId));

        User user = userMapper.toDomain(userEntity);
        Book book = bookMapper.toDomain(bookEntity);

        loanValidator.validateLoanCreation(user, book);

        if (bookEntity.getAvailableStock() <= 0) {
            throw new BookNotAvailableException(
                    "El libro '" + bookEntity.getTitle() + "' no tiene ejemplares disponibles.");
        }

        long activeLoans = loanRepository.countByUserIdAndStatus(
                userId, LoanEntity.LoanStatus.ACTIVE);

        if (activeLoans >= 3) {
            throw new LoanLimitExceededException(
                    "El usuario ya tiene 3 préstamos activos. Debe devolver un libro primero.");
        }

        bookEntity.setAvailableStock(bookEntity.getAvailableStock() - 1);
        bookRepository.save(bookEntity);

        LoanEntity newLoan = LoanEntity.builder()
                .user(userEntity)
                .book(bookEntity)
                .loanDate(DateUtil.getCurrentDate())
                .status(LoanEntity.LoanStatus.ACTIVE)
                .build();

        LoanEntity saved = loanRepository.save(newLoan);
        return loanMapper.toDomain(saved);
    }

    public Loan returnLoan(String userId, String bookId) {
        LoanEntity activeLoan = loanRepository
                .findByUserIdAndBookIdAndStatus(userId, bookId, LoanEntity.LoanStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró ningún préstamo activo para el usuario ID: "
                                + userId + " con el libro ID: " + bookId));

        BookEntity bookEntity = activeLoan.getBook();
        if (bookEntity.getAvailableStock() >= bookEntity.getTotalStock()) {
            throw new IllegalArgumentException(
                    "No se puede devolver: el stock ya está en su máximo.");
        }

        bookEntity.setAvailableStock(bookEntity.getAvailableStock() + 1);
        bookRepository.save(bookEntity);

        activeLoan.setStatus(LoanEntity.LoanStatus.RETURNED);
        activeLoan.setReturnDate(DateUtil.getCurrentDate());
        LoanEntity saved = loanRepository.save(activeLoan);
        return loanMapper.toDomain(saved);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll()
                .stream()
                .map(loanMapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<Loan> getLoansByUser(String userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "No se encuentra ningún usuario con el ID: " + userId));
        return loanRepository.findByUser(userEntity)
                .stream()
                .map(loanMapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<Loan> getLoansByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        "No se encuentra ningún usuario con username: " + username));
        return loanRepository.findByUser(userEntity)
                .stream()
                .map(loanMapper::toDomain)
                .collect(Collectors.toList());
    }
}