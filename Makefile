# NexusAI Backend Makefile (Local Development)

.PHONY: build run test clean help

help:
	@echo "NexusAI - Comandos Disponibles:"
	@echo "  make build        - Compilar el proyecto y empaquetar (saltando tests)"
	@echo "  make run          - Ejecutar la aplicación principal (nexusai-app)"
	@echo "  make test         - Ejecutar todos los tests"
	@echo "  make clean        - Limpiar archivos temporales de compilación"

build:
	./mvnw clean package -DskipTests

run:
	./mvnw spring-boot:run -pl nexusai-app

test:
	./mvnw test

clean:
	./mvnw clean