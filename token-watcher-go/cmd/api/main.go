package main

import (
	"fmt"
	"log"
	"net/http"

	"github.com/bryanalexander-contact/token-watcher/internal/calculator" // Donde vive CalculateTokens
	"github.com/bryanalexander-contact/token-watcher/internal/middleware"
	"github.com/gin-gonic/gin"
)

func main() {
	r := gin.Default()

	// 1. Canal para procesar métricas de FinOps sin bloquear el flujo principal
	// Capacidad de 100 mensajes en buffer
	finopsMetricsChan := make(chan string, 100)

	// 2. Goroutine: Trabajador en segundo plano para reportar gastos
	go func() {
		for msg := range finopsMetricsChan {
			// En el futuro, aquí podrías enviar esto a Kafka o a una DB de logs
			log.Printf("--- 📊 [FINOPS LOG]: %s\n", msg)
		}
	}()

	// 3. Grupo de rutas protegidas por JWT
	v1 := r.Group("/v1")
	v1.Use(middleware.AuthMiddleware()) // <--- Seguridad blindada
	{
		// Endpoint avanzado de Proxy/Stream
		v1.POST("/proxy/stream", func(c *gin.Context) {
			var payload struct {
				Text string `json:"text"`
			}

			if err := c.ShouldBindJSON(&payload); err != nil {
				c.JSON(http.StatusBadRequest, gin.H{"error": "Cuerpo de petición inválido"})
				return
			}

			log.Println("--- 🚀 Go Proxy: Procesando flujo de IA")

			// Cálculo rápido usando nuestro paquete internal
			result := calculator.CalculateTokens(payload.Text)

			// Enviamos al canal de métricas de forma asíncrona
			finopsMetricsChan <- fmt.Sprintf("Usuario procesó %d tokens. Costo: $%f", result.TokenCount, result.FinalCost)

			c.JSON(http.StatusOK, gin.H{
				"status":         "Go Proxy Active",
				"tokens_counted": result.TokenCount,
				"estimated_cost": result.FinalCost,
				"message":        "Métricas enviadas a monitoreo",
			})
		})
	}

	fmt.Println("--- 🏁 Token Watcher en Go (Enterprise) corriendo en el puerto 8081")
	log.Fatal(r.Run(":8081"))
}
