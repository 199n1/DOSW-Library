package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.AuthRequestDTO;
import edu.eci.dosw.tdd.controller.dto.AuthResponseDTO;
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
@Tag(name = "Autenticación", description = "Endpoint público para login y obtención del JWT")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión",
            description = "Recibe username y password, retorna un JWT si las credenciales son válidas")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);
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