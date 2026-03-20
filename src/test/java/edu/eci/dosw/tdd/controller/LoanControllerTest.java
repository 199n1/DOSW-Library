package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.controller.mapper.LoanMapper;
import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;  // ← corregido
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.service.LoanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private LoanService loanService;
    @MockitoBean private LoanMapper loanMapper;

    @Test
    void createLoan_ShouldReturnCreatedLoan() throws Exception {
        Loan loan = new Loan();
        LoanDTO dto = LoanDTO.builder().userId("u1").bookId("b1").status("ACTIVE").build();

        when(loanService.createLoan("u1", "b1")).thenReturn(loan);
        when(loanMapper.toDto(loan)).thenReturn(dto);

        mockMvc.perform(post("/loans")
                        .param("userId", "u1")
                        .param("bookId", "b1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void createLoan_ShouldReturn404_WhenUserNotFound() throws Exception {
        when(loanService.createLoan("u-falso", "b1"))
                .thenThrow(new UserNotFoundException("No existe un usuario registrado con el ID: u-falso"));

        mockMvc.perform(post("/loans")
                        .param("userId", "u-falso")
                        .param("bookId", "b1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createLoan_ShouldReturn409_WhenBookNotAvailable() throws Exception {
        when(loanService.createLoan("u1", "b-agotado"))
                .thenThrow(new BookNotAvailableException("El libro no tiene ejemplares disponibles."));

        mockMvc.perform(post("/loans")
                        .param("userId", "u1")
                        .param("bookId", "b-agotado"))
                .andExpect(status().isConflict());
    }

    @Test
    void returnLoan_ShouldReturnUpdatedLoan() throws Exception {
        Loan loan = new Loan();
        LoanDTO dto = LoanDTO.builder().userId("u1").bookId("b1").status("RETURNED").build();

        when(loanService.returnLoan("u1", "b1")).thenReturn(loan);
        when(loanMapper.toDto(loan)).thenReturn(dto);

        mockMvc.perform(put("/loans/return")
                        .param("userId", "u1")
                        .param("bookId", "b1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETURNED"));
    }

    @Test
    void getAllLoans_ShouldReturnList() throws Exception {
        Loan loan = new Loan();
        LoanDTO dto = LoanDTO.builder().userId("u1").bookId("b1").build();

        when(loanService.getAllLoans()).thenReturn(Collections.singletonList(loan));
        when(loanMapper.toDto(loan)).thenReturn(dto);

        mockMvc.perform(get("/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("u1"));
    }
}