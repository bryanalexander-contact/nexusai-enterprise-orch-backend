package com.nexusai.core.config.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    // LLAVE IMPORTANTE: Debe ser larga para HS256
    private static final String SECRET_KEY = "tu_llave_secreta_super_larga_para_nexus_ai_finops_2026";
    private static final long EXPIRATION_TIME = 86400000; // 24 horas

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // Genera el token usando el ID del usuario
    public String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Extrae el ID del usuario para que el filtro sepa quién es
    public String extractUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}