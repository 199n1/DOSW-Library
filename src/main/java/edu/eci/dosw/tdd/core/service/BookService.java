package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.util.IdGeneratorUtil;
import edu.eci.dosw.tdd.core.Validator.BookValidator;
import edu.eci.dosw.tdd.persistence.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.mapper.BookPersistenceMapper;
import edu.eci.dosw.tdd.persistence.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookValidator bookValidator;
    private final BookRepository bookRepository;
    private final BookPersistenceMapper bookMapper;

    public Book addBook(Book book, int quantity) {
        bookValidator.validate(book);
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de ejemplares debe ser mayor a 0.");
        }
        if (book.getId() == null) {
            book.setId(IdGeneratorUtil.generateId());
        }
        book.setTotalStock(quantity);
        book.setAvailableStock(quantity);
        BookEntity saved = bookRepository.save(bookMapper.toEntity(book));
        return bookMapper.toDomain(saved);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Optional<Book> getBookById(String id) {
        return bookRepository.findById(id)
                .map(bookMapper::toDomain);
    }

    public boolean isBookAvailable(Book book) {
        return bookRepository.findById(book.getId())
                .map(entity -> entity.getAvailableStock() > 0)
                .orElse(false);
    }

    public void updateBookAvailability(Book book, int quantityChange) {
        BookEntity entity = bookRepository.findById(book.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Libro no encontrado con ID: " + book.getId()));

        int newAvailable = entity.getAvailableStock() + quantityChange;

        if (newAvailable < 0) {
            throw new IllegalArgumentException(
                    "La cantidad disponible no puede ser menor a 0.");
        }
        if (newAvailable > entity.getTotalStock()) {
            throw new IllegalArgumentException(
                    "La cantidad disponible no puede superar el stock total.");
        }

        entity.setAvailableStock(newAvailable);
        bookRepository.save(entity);
    }
}