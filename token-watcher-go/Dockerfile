# Etapa 1: Construcción
FROM golang:1.25.6-alpine AS builder
WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download
COPY . .
# Compilamos apuntando al archivo main exacto
RUN go build -o main ./cmd/api/main.go 

# Etapa 2: Ejecución
FROM alpine:latest
RUN apk --no-cache add ca-certificates
WORKDIR /root/
COPY --from=builder /app/main .
EXPOSE 8081
CMD ["./main"]