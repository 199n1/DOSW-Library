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
@Tag(name = "Libros", description = "Procesos de inventario y busqueda de libros")
public class BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;

    @PostMapping
    @Operation(summary = "Añadir un libro", description = "Registra un uevo libro en el inventario con la cantidad de ejemplares")
    public ResponseEntity<BookDTO> addBook(@RequestBody BookDTO bookDTO) {
        Book book = bookMapper.toEntity(bookDTO);
        Book createdBook = bookService.addBook(book, bookDTO.getInitialQuantity());
        return new ResponseEntity<>(bookMapper.toDto(createdBook), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Consigue inventario completo", description = "Retorna todos los libros registrados con su cantidad disponible")
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
    @Operation(summary = "Buscar libro por ID", description = "da la información de un libro específico dado su ID")
    public ResponseEntity<BookDTO> getBookById(@PathVariable String id) {
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró ningún libro con el ID: " + id));
        return ResponseEntity.ok(bookMapper.toDto(book));
    }
}