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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Operaciones de registro y consulta de usuarios")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    // FIX: publico para poder crear el primer usuario/admin
    // Si quieres restringirlo solo a LIBRARIAN, cambia a @PreAuthorize("hasRole('LIBRARIAN')")
    @PostMapping
    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario en el sistema de biblioteca")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User createdUser = userService.registerUser(user);
        return new ResponseEntity<>(userMapper.toDto(createdUser), HttpStatus.CREATED);
    }

    // FIX: solo LIBRARIAN puede listar todos los usuarios
    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    @Operation(summary = "Listar usuarios", description = "Retorna los usuarios registrados en el sistema")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    // FIX: solo LIBRARIAN puede buscar usuarios por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    @Operation(summary = "Busca usuario por ID", description = "Retorna la informacion de un usuario dado su ID")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontro ningun usuario con el ID: " + id));
        return ResponseEntity.ok(userMapper.toDto(user));
    }
}