package edu.eci.dosw.tdd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.controller.mapper.LoanMapper;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.service.LoanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoanService loanService;

    @MockitoBean
    private LoanMapper loanMapper;

    @Test
    void createLoan_ShouldReturnCreatedLoan() throws Exception {
        Loan loan = new Loan();
        LoanDTO responseDTO = LoanDTO.builder().userId("u1").bookId("b1").status("ACTIVE").build();

        when(loanService.createLoan("u1", "b1")).thenReturn(loan);
        when(loanMapper.toDto(loan)).thenReturn(responseDTO);

        mockMvc.perform(post("/loans")
                        .param("userId", "u1")
                        .param("bookId", "b1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("u1"))
                .andExpect(jsonPath("$.bookId").value("b1"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void returnLoan_ShouldReturnUpdatedLoan() throws Exception {
        Loan loan = new Loan();
        LoanDTO responseDTO = LoanDTO.builder().userId("u1").bookId("b1").status("RETURNED").build();

        when(loanService.returnLoan("u1", "b1")).thenReturn(loan);
        when(loanMapper.toDto(loan)).thenReturn(responseDTO);

        mockMvc.perform(put("/loans/return")
                        .param("userId", "u1")
                        .param("bookId", "b1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETURNED"));
    }

    @Test
    void getAllLoans_ShouldReturnList() throws Exception {
        Loan loan = new Loan();
        LoanDTO responseDTO = LoanDTO.builder().userId("u1").bookId("b1").build();

        when(loanService.getAllLoans()).thenReturn(Collections.singletonList(loan));
        when(loanMapper.toDto(loan)).thenReturn(responseDTO);

        mockMvc.perform(get("/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("u1"));
    }
}
