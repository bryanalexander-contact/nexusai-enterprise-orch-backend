package com.nexusai.core.controller.auth;

import com.nexusai.core.config.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        // En un sistema real, aquí validas contra la DB con PasswordEncoder
        // Por ahora, si el email existe, generamos token para el Usuario ID: 1
        
        String token = jwtService.generateToken("1"); // Usamos el ID como 'subject'
        
        return ResponseEntity.ok(Map.of(
            "access_token", token,
            "token_type", "Bearer"
        ));
    }
}