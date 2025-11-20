package com.fiap.nova.controller;

import com.fiap.nova.service.ChatbotService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "*")
public class ChatbotController {

    private final ChatbotService perplexityService;

    public ChatbotController(ChatbotService perplexityService) {
        this.perplexityService = perplexityService;
    }

    @PostMapping("/ask")
    public Map<String, String> ask(@RequestBody Map<String, Object> json) {

        Long userId = Long.valueOf(json.get("userId").toString());
        String message = json.get("message").toString();

        String answer = perplexityService.chatWithAI(userId, message);

        return Map.of("answer", answer);
    }
}