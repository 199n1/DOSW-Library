package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.controller.mapper.BookMapper;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Libros", description = "Procesos de inventario y búsqueda de libros")
public class BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;

    @PostMapping
    @Operation(summary = "Añadir un libro")
    public ResponseEntity<BookDTO> addBook(@RequestBody BookDTO bookDTO) {
        Book book = bookMapper.toEntity(bookDTO);
        Book created = bookService.addBook(book, bookDTO.getTotalStock());
        return new ResponseEntity<>(bookMapper.toDto(created), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los libros")
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<BookDTO> books = bookService.getAllBooks()
                .stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar libro por ID")
    public ResponseEntity<BookDTO> getBookById(@PathVariable String id) {
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró ningún libro con el ID: " + id));
        return ResponseEntity.ok(bookMapper.toDto(book));
    }
}