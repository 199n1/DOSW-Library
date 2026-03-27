package edu.eci.dosw.tdd.controller.mapper;

import edu.eci.dosw.tdd.controller.dto.UserDTO;
import edu.eci.dosw.tdd.core.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserDTO dto) {
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .role(dto.getRole() != null ? dto.getRole() : "USER")
                .build();
    }

    public UserDTO toDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}