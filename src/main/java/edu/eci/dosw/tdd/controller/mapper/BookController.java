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
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Libros", description = "Operaciones de inventario y consulta de libros")
public class BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;

    @PostMapping
    @Operation(summary = "Agregar un libro", description = "Añade un libro al inventario con su cantidad inicial")
    public ResponseEntity<BookDTO> addBook(@RequestBody BookDTO bookDTO) {
        Book book = bookMapper.toEntity(bookDTO);
        Book createdBook = bookService.addBook(book, bookDTO.getInitialQuantity());
        return new ResponseEntity<>(bookMapper.toDto(createdBook), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Obtener inventario", description = "Devuelve el catálogo de libros")
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        Map<Book, Integer> inventory = bookService.getAllBooks();
        List<BookDTO> books = inventory.entrySet().stream()
                .map(entry -> {
                    BookDTO dto = bookMapper.toDto(entry.getKey());
                    dto.setInitialQuantity(entry.getValue());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar libro por ID", description = "Busca la información detallada de un libro")
    public ResponseEntity<BookDTO> getBookById(@PathVariable String id) {
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado con ID: " + id));
        return ResponseEntity.ok(bookMapper.toDto(book));
    }
}