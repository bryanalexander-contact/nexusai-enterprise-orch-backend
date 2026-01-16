package com.nexusai.core.controller.ai;

import com.nexusai.core.controller.dto.ChatRequest;
import com.nexusai.core.controller.dto.ChatResponse;
import com.nexusai.core.domain.service.AiOrchestrator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class ChatController {

    private final AiOrchestrator aiOrchestrator;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> askGemini(@io.swagger.v3.oas.annotations.parameters.RequestBody @Valid @RequestBody ChatRequest request) {
        
        // Llamamos al orquestador que maneja el cobro y la IA
        String aiResult = aiOrchestrator.handleSmartQuery(request.userId(), request.message());
        
        return ResponseEntity.ok(new ChatResponse(aiResult, "SUCCESS"));
    }
}