package edu.eci.dosw.tdd.core.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private String id;
    private String name;
    private String username;
    private String password;
    private String role;
}