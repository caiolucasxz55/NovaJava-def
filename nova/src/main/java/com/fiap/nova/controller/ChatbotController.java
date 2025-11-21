package com.fiap.nova.controller;

import com.fiap.nova.dto.ChatMessageResponse;
import com.fiap.nova.service.ChatbotService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
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
        log.info("Returning answer to user");
        String answer = perplexityService.chatWithAI(userId, message);

        return Map.of("answer", answer);
    }

    @GetMapping("/history")
    @Operation(summary = "Get chat history")
    public List<ChatMessageResponse> getHistory(@RequestParam Long userId) {
        log.info("Fetching chat history for userId: {}", userId);
        return perplexityService.getUserHistory(userId);
    }
}