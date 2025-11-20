package com.fiap.nova.service;

import com.fiap.nova.model.AIInteraction;
import com.fiap.nova.model.User;
import com.fiap.nova.model.Skill;
import com.fiap.nova.repository.AIInteractionRepository;
import com.fiap.nova.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final AIInteractionRepository aiInteractionRepository;
    private final UserRepository userRepository;

    @Value("${perplexity.api.key}")
    private String apiKey;

    @Value("${perplexity.api.url:https://api.perplexity.ai/chat/completions}")
    private String apiUrl;

    private static final int MEMORY_SIZE = 6; 

    // Transactional is required to lazily fetch Skills and Goals from the database
    @Transactional
    public String chatWithAI(Long userId, String userMessage) {

        // API Key validation
        String cleanKey = apiKey != null ? apiKey.trim() : "";
        if (cleanKey.isEmpty() || cleanKey.contains("default-key-not-set")) {
            throw new RuntimeException("CONFIGURATION ERROR: PERPLEXITY_API_KEY missing.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // HTTP Setup
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(cleanKey);

        List<String> jsonMessages = new ArrayList<>();

        // Build AI Persona with context
        String systemPersona = buildSystemPersona(user);
        jsonMessages.add("""
            { "role": "system", "content": "%s" }
            """.formatted(escapeJson(systemPersona)).trim());

        // Add conversation history
        List<String> historyMessages = getHistoryAsJsonList(user);
        jsonMessages.addAll(historyMessages);

        // Add current message
        jsonMessages.add("""
            { "role": "user", "content": "%s" }
            """.formatted(escapeJson(userMessage)).trim());

        String allMessagesJson = String.join(",\n", jsonMessages);

        // Request Body
        String requestBody = """
        {
          "model": "sonar-pro",
          "messages": [
            %s
          ]
        }
        """.formatted(allMessagesJson);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);
            
            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || !responseBody.containsKey("choices")) {
                throw new RuntimeException("Empty response from AI");
            }

            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> messageMap = (Map<String, Object>) firstChoice.get("message");
            String aiResponseContent = (String) messageMap.get("content");

            // Save interaction
            AIInteraction interaction = new AIInteraction();
            interaction.setUser(user);
            interaction.setUserMessage(userMessage);
            interaction.setAiResponse(aiResponseContent);
            aiInteractionRepository.save(interaction);

            return aiResponseContent;

        } catch (HttpClientErrorException e) {
            System.err.println("API ERROR: " + e.getResponseBodyAsString());
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) throw new RuntimeException("Error 401: Invalid Key.");
            throw new RuntimeException("AI API Error: " + e.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Internal Error: " + e.getMessage());
        }
    }

    private String buildSystemPersona(User user) {
        String name = user.getName();
        String objective = user.getProfessionalGoal() != null ? user.getProfessionalGoal() : "Não definido";
        
        // Extract Skills
        String skills = "Nenhuma cadastrada";
        if (user.getSkills() != null && !user.getSkills().isEmpty()) {
            skills = user.getSkills().stream()
                    .map((Skill s) -> {
                        String tech = s.getTechnicalSkill();
                        String soft = s.getSoftSkill();
                        if (soft != null && !soft.isBlank()) {
                            return tech + " (" + soft + ")";
                        }
                        return tech;
                    })
                    .collect(Collectors.joining(", "));
        }

        // Extract Goals
        String goals = "Nenhuma cadastrada";
        if (user.getGoals() != null && !user.getGoals().isEmpty()) {
            goals = user.getGoals().stream()
                    .map(Object::toString) // Fallback to toString if specific getter is unknown
                    .collect(Collectors.joining("; "));
        }

        // AI Persona definition
        return """
            Você é a NOVA, assistente inteligente de carreira do sistema FIAP.
            
            PERFIL DO USUÁRIO (CONTEXTO REAL):
            - Nome: %s
            - Objetivo Profissional: %s
            - Habilidades (Skills): %s
            - Metas Atuais: %s
            
            DIRETRIZES:
            1. Analise as metas e skills do usuário para dar conselhos úteis.
            2. Seja motivadora e profissional.
            3. Use Markdown (**negrito**, listas) para formatar.
            4. NÃO use citações numéricas como [1][2].
            """.formatted(name, objective, skills, goals);
    }

    private List<String> getHistoryAsJsonList(User user) {
        PageRequest pageRequest = PageRequest.of(0, MEMORY_SIZE, Sort.by(Sort.Direction.DESC, "id"));
        List<AIInteraction> history = aiInteractionRepository.findByUser(user, pageRequest);
        Collections.reverse(history);

        List<String> messages = new ArrayList<>();
        for (AIInteraction h : history) {
            messages.add("""
                { "role": "user", "content": "%s" }""".formatted(escapeJson(h.getUserMessage())).trim());
            if (h.getAiResponse() != null) {
                messages.add("""
                    { "role": "assistant", "content": "%s" }""".formatted(escapeJson(h.getAiResponse())).trim());
            }
        }
        return messages;
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "");
    }
}