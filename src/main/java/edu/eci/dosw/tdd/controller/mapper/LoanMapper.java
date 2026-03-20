package edu.eci.dosw.tdd.controller.mapper;

import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.core.model.Loan;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {

    public LoanDTO toDto(Loan loan) {
        return LoanDTO.builder()
                .userId(loan.getUser() != null ? loan.getUser().getId() : null)
                .bookId(loan.getBook() != null ? loan.getBook().getId() : null)
                .loanDate(loan.getLoanDate())
                .status(loan.getStatus() != null ? loan.getStatus().name() : null)
                .returnDate(loan.getReturnDate())
                .build();
    }
}
