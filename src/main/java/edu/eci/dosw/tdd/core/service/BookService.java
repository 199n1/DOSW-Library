package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.util.idGeneratorUtil;
import edu.eci.dosw.tdd.core.validator.BookValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookValidator bookValidator;
    private final Map<Book, Integer> bookInventory = new HashMap<>();

    public Book addBook(Book book, int quantity) {
        bookValidator.validate(book);

        if (book.getId() == null) {
            book.setId(idGeneratorUtil.generateId());
        }

        bookInventory.put(book, bookInventory.getOrDefault(book, 0) + quantity);
        return book;
    }

    public Map<Book, Integer> getAllBooks() {
        return bookInventory;
    }

    public Optional<Book> getBookById(String id) {
        return bookInventory.keySet().stream()
                .filter(b -> b.getId().equals(id))
                .findFirst();
    }

    public boolean isBookAvailable(Book book) {
        return bookInventory.getOrDefault(book, 0) > 0;
    }

    public void updateBookAvailability(Book book, int quantityChange) {
        int currentQuantity = bookInventory.getOrDefault(book, 0);
        bookInventory.put(book, currentQuantity + quantityChange);
    }
}