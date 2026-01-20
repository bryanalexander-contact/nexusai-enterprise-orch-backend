package calculator

import (
	"strings"
)

// Result representa el cálculo final del stream
type Result struct {
	TokenCount int
	FinalCost  float64
}

// CalculateTokens analiza el texto de forma ultra rápida
func CalculateTokens(text string) Result {
	// En un escenario real aquí usaríamos "github.com/pkoukk/tiktoken-go"
	// para ser exactos con los modelos de Google/OpenAI
	words := strings.Fields(text)
	count := len(words)

	return Result{
		TokenCount: count,
		FinalCost:  float64(count) * 0.00002, // Corregido: FinalCost en lugar de Cost
	}
}
