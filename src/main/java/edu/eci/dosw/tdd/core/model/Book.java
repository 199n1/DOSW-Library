package edu.eci.dosw.tdd.core.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
    private String id;
    private String title;
    private String author;
    private int totalStock;
    private int availableStock;
}