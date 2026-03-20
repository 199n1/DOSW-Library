package edu.eci.dosw.tdd.core.Validator;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.User;
import org.springframework.stereotype.Component;

@Component
public class LoanValidator {
    public void validateLoanCreation(User user, Book book) {
        if (user == null) {
            throw new IllegalArgumentException("Se necesita el usuario para realizar el prestamo");
        }
        if (book == null) {
            throw new IllegalArgumentException("Se necesita el libro para realizar el prestamo");
        }
    }
}