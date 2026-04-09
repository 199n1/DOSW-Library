package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.AuthRequestDTO;
import edu.eci.dosw.tdd.controller.dto.AuthResponseDTO;
import edu.eci.dosw.tdd.persistence.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
import edu.eci.dosw.tdd.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticacion", description = "Endpoint publico para login y obtencion del JWT")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion",
            description = "Recibe username y password, retorna un JWT si las credenciales son validas")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // FIX: obtener el userId real desde la BD para incluirlo en el token
        String userId = userRepository.findByUsername(userDetails.getUsername())
                .map(UserEntity::getId)
                .orElse(null);

        String token = jwtService.generateToken(userDetails, userId);
        String role = userDetails.getAuthorities()
                .iterator().next().getAuthority()
                .replace("ROLE_", "");

        return ResponseEntity.ok(AuthResponseDTO.builder()
                .token(token)
                .role(role)
                .username(userDetails.getUsername())
                .build());
    }
}