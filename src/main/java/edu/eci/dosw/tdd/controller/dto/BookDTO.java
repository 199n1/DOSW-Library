package edu.eci.dosw.tdd.controller.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDTO {
    private String id;
    private String title;
    private String author;
    private int totalStock;
    private int availableStock;
}