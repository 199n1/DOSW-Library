package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.controller.mapper.LoanMapper;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
@Tag(name = "Préstamos", description = "Administración de préstamos y devoluciones de libros")
public class LoanController {

    private final LoanService loanService;
    private final LoanMapper loanMapper;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('LIBRARIAN')")
    @Operation(summary = "Crear un préstamo",
            description = "Anota el préstamo de un libro a un usuario")
    public ResponseEntity<LoanDTO> createLoan(@RequestParam String userId,
                                              @RequestParam String bookId) {
        Loan newLoan = loanService.createLoan(userId, bookId);
        return new ResponseEntity<>(loanMapper.toDto(newLoan), HttpStatus.CREATED);
    }

    @PutMapping("/return")
    @PreAuthorize("hasRole('USER') or hasRole('LIBRARIAN')")
    @Operation(summary = "Devolver un libro",
            description = "Registra la devolución de un libro previamente prestado")
    public ResponseEntity<LoanDTO> returnLoan(@RequestParam String userId,
                                              @RequestParam String bookId) {
        Loan returnedLoan = loanService.returnLoan(userId, bookId);
        return ResponseEntity.ok(loanMapper.toDto(returnedLoan));
    }

    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    @Operation(summary = "Historial completo de préstamos",
            description = "Retorna todos los préstamos registrados, activos y devueltos")
    public ResponseEntity<List<LoanDTO>> getAllLoans() {
        List<LoanDTO> loans = loanService.getAllLoans().stream()
                .map(loanMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Mis préstamos",
            description = "Retorna únicamente los préstamos del usuario autenticado")
    public ResponseEntity<List<LoanDTO>> getMyLoans(Authentication authentication) {
        String username = authentication.getName();
        List<LoanDTO> loans = loanService.getLoansByUsername(username).stream()
                .map(loanMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(loans);
    }
}