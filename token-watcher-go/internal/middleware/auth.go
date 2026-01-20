package middleware

import (
    "net/http"
    "strings"

    "github.com/gin-gonic/gin"
    "github.com/golang-jwt/jwt/v5"
)

// Usa la misma llave que definiste en Java (JwtService.java)
var jwtKey = []byte("tu_llave_secreta_super_larga_para_nexus_ai_finops_2026")

func AuthMiddleware() gin.HandlerFunc {
    return func(c *gin.Context) {
        authHeader := c.GetHeader("Authorization")
        if authHeader == "" {
            c.JSON(http.StatusUnauthorized, gin.H{"error": "Authorization header is required"})
            c.Abort()
            return
        }

        tokenStr := strings.TrimPrefix(authHeader, "Bearer ")
        
        // Parsear el token
        token, err := jwt.Parse(tokenStr, func(token *jwt.Token) (interface{}, error) {
            // Validar el método de firma (opcional pero recomendado)
            if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
                return nil, http.ErrAbortHandler
            }
            return jwtKey, nil
        })

        if err != nil || token == nil || !token.Valid {
            c.JSON(http.StatusUnauthorized, gin.H{"error": "Invalid or expired token"})
            c.Abort()
            return
        }

        // Si el token es válido, continuamos
        c.Next()
    }
}