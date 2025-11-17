package com.fiap.nova.service;

import com.fiap.nova.model.AIInteraction;
import com.fiap.nova.model.User;
import com.fiap.nova.repository.AIInteractionRepository;
import com.fiap.nova.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PerplexityService {

    private final AIInteractionRepository aiInteractionRepository;
    private final UserRepository userRepository;

    @Value("${perplexity.api.key}")
    private String apiKey;

    @Value("${perplexity.api.url:https://api.perplexity.ai/chat/completions}")
    private String apiUrl;

    public String chatWithAI(Long userId, String userMessage) {

        // Debug: verificar se a chave está sendo carregada
        System.out.println("API Key carregada: " + (apiKey != null && !apiKey.isBlank() ? "SIM (primeiros 10 chars: " + apiKey.substring(0, Math.min(10, apiKey.length())) + "...)" : "NÃO - VAZIA OU NULL"));
        System.out.println("API URL: " + apiUrl);

        // Busca usuário no banco
        User usuario = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + userId));

        // Configura requisição HTTP
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(apiKey);
        headers.set("User-Agent", "NOVA-Chatbot/1.0");

        String body = """
        {
          "model": "sonar-pro",
          "messages": [
            { "role": "user", "content": "%s" }
          ]
        }
        """.formatted(userMessage);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

        // 🔹 3. Extrai resposta da IA
        Map resp = response.getBody();
        Map firstChoice = ((List<Map>) resp.get("choices")).get(0);
        Map message = (Map) firstChoice.get("message");
        String respostaIA = (String) message.get("content");

        // 🔹 4. Salva interação no banco (LOCALDATE é gerado automaticamente)
        AIInteraction interacao = AIInteraction.builder()
                .user(usuario)
                .userMessage(userMessage)
                .aiResponse(respostaIA)
                .build();

        aiInteractionRepository.save(interacao);

        return respostaIA;
    }
}