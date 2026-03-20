package com.nexusai.core.ai.controller;

import com.nexusai.core.ai.domain.service.AiOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class ChatController {

    private final AiOrchestrator aiOrchestrator;

    @PostMapping("/chat")
    public String chat(@RequestParam Long userId, @RequestBody String prompt) {
        return aiOrchestrator.handleSmartQuery(userId, prompt);
    }
}
