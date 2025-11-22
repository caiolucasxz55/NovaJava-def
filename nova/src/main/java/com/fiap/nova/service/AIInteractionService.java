










package com.fiap.nova.service;

import org.springframework.stereotype.Service;
import com.fiap.nova.model.AIInteraction;
import com.fiap.nova.repository.AIInteractionRepository;

@Service
public class AIInteractionService {
    private final AIInteractionRepository aiInteractionRepository;

    public AIInteractionService(AIInteractionRepository aiInteractionRepository) {
        this.aiInteractionRepository = aiInteractionRepository;
    }

    public AIInteraction save(AIInteraction interaction) {
        return aiInteractionRepository.save(interaction);
    }
}
