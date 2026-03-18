package edu.eci.dosw.tdd.core.validator;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.util.ValidationUtil;
import org.springframework.stereotype.Component;

@Component
public class BookValidator {
    public void validate(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("El libro no puede ser nulo");
        }
        if (ValidationUtil.isNullOrEmpty(book.getTitle()) || ValidationUtil.isNullOrEmpty(book.getAuthor())) {
            throw new IllegalArgumentException("El título y el autor del libro son obligatorios");
        }
    }
}