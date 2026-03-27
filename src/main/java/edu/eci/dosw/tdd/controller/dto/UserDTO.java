package edu.eci.dosw.tdd.controller.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private String id;
    private String name;
    private String username;
    private String password;
    private String role;
}