package edu.eci.dosw.tdd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.controller.mapper.BookMapper;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.service.BookService;
import edu.eci.dosw.tdd.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private BookService bookService;
    @MockitoBean private BookMapper bookMapper;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void addBook_ShouldReturnCreatedBook() throws Exception {
        BookDTO request = BookDTO.builder()
                .title("Clean Code").author("Uncle Bob").totalStock(5).availableStock(5).build();
        Book book = Book.builder()
                .id("b1").title("Clean Code").author("Uncle Bob").totalStock(5).availableStock(5).build();
        BookDTO response = BookDTO.builder()
                .id("b1").title("Clean Code").author("Uncle Bob").totalStock(5).availableStock(5).build();

        when(bookMapper.toEntity(any())).thenReturn(book);
        when(bookService.addBook(any(), anyInt())).thenReturn(book);
        when(bookMapper.toDto(any())).thenReturn(response);

        mockMvc.perform(post("/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("b1"))
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.totalStock").value(5));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllBooks_ShouldReturnList() throws Exception {
        Book book = Book.builder()
                .id("b1").title("Clean Code").author("Uncle Bob").totalStock(10).availableStock(10).build();
        BookDTO dto = BookDTO.builder()
                .id("b1").title("Clean Code").author("Uncle Bob").totalStock(10).availableStock(10).build();

        when(bookService.getAllBooks()).thenReturn(Collections.singletonList(book));
        when(bookMapper.toDto(book)).thenReturn(dto);

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("b1"))
                .andExpect(jsonPath("$[0].totalStock").value(10));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getBookById_ShouldReturnBook_WhenExists() throws Exception {
        Book book = Book.builder().id("b1").title("Clean Code").build();
        BookDTO dto = BookDTO.builder().id("b1").title("Clean Code").build();

        when(bookService.getBookById("b1")).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(dto);

        mockMvc.perform(get("/books/b1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("b1"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getBookById_ShouldReturn400_WhenBookNotFound() throws Exception {
        when(bookService.getBookById("id-falso")).thenReturn(Optional.empty());

        mockMvc.perform(get("/books/id-falso"))
                .andExpect(status().isBadRequest());
    }
}