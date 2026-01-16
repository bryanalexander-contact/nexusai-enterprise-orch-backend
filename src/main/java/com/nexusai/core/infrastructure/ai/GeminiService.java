package com.nexusai.core.infrastructure.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final ChatModel chatModel;

    public String generateResponse(String prompt) {
        try {
            return chatModel.call(new Prompt(prompt))
                            .getResult()
                            .getOutput()
                            .getText();
        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con Gemini", e);
        }
    }
}
