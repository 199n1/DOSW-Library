package edu.eci.dosw.tdd.core.Validator;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.User;
import org.springframework.stereotype.Component;

@Component
public class LoanValidator {
    public void validateLoanCreation(User user, Book book) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario es requerido para el préstamo");
        }
        if (book == null) {
            throw new IllegalArgumentException("El libro es requerido para el préstamo");
        }
    }
}