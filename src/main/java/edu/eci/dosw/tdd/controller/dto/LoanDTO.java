package edu.eci.dosw.tdd.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class LoanDTO {
    private String userId;
    private String bookId;
    private LocalDate loanDate;
    private String status;
    private LocalDate returnDate;
}