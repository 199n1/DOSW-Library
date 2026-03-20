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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
@Tag(name = "Préstamos", description = "Gestión de préstamos y devoluciones")
public class LoanController {

    private final LoanService loanService;
    private final LoanMapper loanMapper;

    @PostMapping
    @Operation(summary = "Crear un préstamo", description = "Asigna un libro a un usuario")
    public ResponseEntity<LoanDTO> createLoan(@RequestParam String userId, @RequestParam String bookId) {
        Loan newLoan = loanService.createLoan(userId, bookId);
        return new ResponseEntity<>(loanMapper.toDto(newLoan), HttpStatus.CREATED);
    }

    @PutMapping("/return")
    @Operation(summary = "Devolver un libro", description = "Registra la devolución de un libro prestado")
    public ResponseEntity<LoanDTO> returnLoan(@RequestParam String userId, @RequestParam String bookId) {
        Loan returnedLoan = loanService.returnLoan(userId, bookId);
        return ResponseEntity.ok(loanMapper.toDto(returnedLoan));
    }

    @GetMapping
    @Operation(summary = "Obtener historial de préstamos", description = "Devuelve la lista de préstamos activos e inactivos")
    public ResponseEntity<List<LoanDTO>> getAllLoans() {
        List<LoanDTO> loans = loanService.getAllLoans().stream()
                .map(loanMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(loans);
    }
}