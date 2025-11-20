package com.fiap.nova.repository;

import com.fiap.nova.model.AIInteraction;
import com.fiap.nova.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AIInteractionRepository extends JpaRepository<AIInteraction, Long> {

    List<AIInteraction> findByUser(User user, Pageable pageable);
}