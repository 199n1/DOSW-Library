package edu.eci.dosw.tdd.persistence.mapper;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.entity.BookEntity;
import org.springframework.stereotype.Component;

@Component
public class BookPersistenceMapper {

    public BookEntity toEntity(Book book) {
        return BookEntity.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .totalStock(book.getTotalStock())
                .availableStock(book.getAvailableStock())
                .build();
    }

    public Book toDomain(BookEntity entity) {
        return Book.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .author(entity.getAuthor())
                .totalStock(entity.getTotalStock())
                .availableStock(entity.getAvailableStock())
                .build();
    }
}