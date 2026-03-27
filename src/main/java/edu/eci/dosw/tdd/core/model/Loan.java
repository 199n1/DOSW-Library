package edu.eci.dosw.tdd.core.model;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {
    private String id;
    private Book book;
    private User user;
    private LocalDate loanDate;
    private LoanStatus status;
    private LocalDate returnDate;
}