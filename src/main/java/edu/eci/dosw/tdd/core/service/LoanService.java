package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.util.DateUtil;
import edu.eci.dosw.tdd.core.Validator.LoanValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final UserService userService;
    private final BookService bookService;
    private final LoanValidator loanValidator;
    private final List<Loan> loans = new ArrayList<>();

    public Loan createLoan(String userId, String bookId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "No se encuantra ningun usuario registrado con el ID: " + userId));

        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encuantra ningun usuario registrado con el ID: " + bookId));

        loanValidator.validateLoanCreation(user, book);

        if (!bookService.isBookAvailable(book)) {
            throw new BookNotAvailableException(
                    "El libro '" + book.getTitle() + "' no tiene ejemplares disponibles en este momento.");
        }

        long activeLoans = loans.stream()
                .filter(l -> l.getUser().getId().equals(userId)
                        && l.getStatus() == LoanStatus.ACTIVE)
                .count();

        if (activeLoans >= 3) {
            throw new LoanLimitExceededException(
                    "El usuario ya tiene 3 préstamos activos. tiene que devolver un libro para poder solicitar otro.");
        }

        Loan newLoan = Loan.builder()
                .book(book)
                .user(user)
                .loanDate(DateUtil.getCurrentDate())
                .status(LoanStatus.ACTIVE)
                .build();

        loans.add(newLoan);
        bookService.updateBookAvailability(book, -1);
        return newLoan;
    }

    public Loan returnLoan(String userId, String bookId) {
        Loan activeLoan = loans.stream()
                .filter(loan -> loan.getUser().getId().equals(userId)
                        && loan.getBook().getId().equals(bookId)
                        && loan.getStatus() == LoanStatus.ACTIVE)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró ningun préstamo activo para el usuario ID: "
                                + userId + " con el libro ID: " + bookId));

        activeLoan.setStatus(LoanStatus.RETURNED);
        activeLoan.setReturnDate(DateUtil.getCurrentDate());
        bookService.updateBookAvailability(activeLoan.getBook(), 1);
        return activeLoan;
    }

    public List<Loan> getAllLoans() {
        return loans;
    }
}