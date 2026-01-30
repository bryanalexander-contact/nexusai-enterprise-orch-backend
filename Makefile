# Variables
COMPOSE = docker compose

.PHONY: up down restart logs ps clean

# Levantar todo en modo silencioso
up:
	$(COMPOSE) up -d

# Construir y levantar (cuando cambias código)
build:
	$(COMPOSE) up --build -d

# Detener todo
down:
	$(COMPOSE) down --remove-orphans

# Ver logs en tiempo real de toda la app
logs:
	$(COMPOSE) logs -f

# Limpiar datos de las bases de datos (Reset total)
clean:
	$(COMPOSE) down --volumes --remove-orphans
	rm -rf infra/postgres_data/*
	rm -rf infra/chroma_data/*

# Estado de los contenedores
ps:
	$(COMPOSE) ps