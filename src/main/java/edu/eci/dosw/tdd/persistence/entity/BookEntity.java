package edu.eci.dosw.tdd.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookEntity {

    @Id
    @Column(nullable = false, unique = true)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(name = "total_stock", nullable = false)
    private int totalStock;

    @Column(name = "available_stock", nullable = false)
    private int availableStock;
}