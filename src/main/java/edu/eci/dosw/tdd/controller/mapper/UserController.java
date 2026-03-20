package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.UserDTO;
import edu.eci.dosw.tdd.controller.mapper.UserMapper;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Operaciones relacionadas con los usuarios")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    @Operation(summary = "Crear un nuevo usuario", description = "Registra un usuario en el sistema")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User createdUser = userService.registerUser(user);
        return new ResponseEntity<>(userMapper.toDto(createdUser), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios", description = "Devuelve la lista de usuarios registrados")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Busca un usuario por su identificador único")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        return ResponseEntity.ok(userMapper.toDto(user));
    }
}