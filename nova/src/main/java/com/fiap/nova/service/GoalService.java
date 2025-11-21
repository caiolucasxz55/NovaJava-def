package com.fiap.nova.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fiap.nova.model.Goal;
import com.fiap.nova.model.User;
import com.fiap.nova.repository.GoalRepository;
import com.fiap.nova.repository.UserRepository;

import java.util.List;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public GoalService(GoalRepository goalRepository, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    public Page<Goal> listGoalsByUser(Long userId, Pageable pageable) {
        if (userId == null) {
            return Page.empty();
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        List<Goal> userGoals = user.getGoals();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), userGoals.size());
        
        List<Goal> pageContent = (start > userGoals.size()) ? List.of() : userGoals.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, userGoals.size());
    }

    public Goal createGoal(Goal goal, Long userId) {
        Goal savedGoal = goalRepository.save(goal);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.getGoals().add(savedGoal);
        userRepository.save(user);
        return savedGoal;
    }

    public List<Goal> listAll() {
        return goalRepository.findAll();
    }

    public Page<Goal> listAllPaginated(Pageable pageable) {
        return goalRepository.findAll(pageable);
    }

    public Goal getGoalById(Long id) {
        return goalRepository.findById(id)
                    .orElse(null);
    }

    public Goal updateGoal(Long goalId, Long userId, Goal goalDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Goal goal = getGoalById(goalId);
        if (goal == null) {
            throw new RuntimeException("Goal not found: " + goalId);
        }
        
        // Verifica se a goal pertence ao usuário
        if (!user.getGoals().contains(goal)) {
            throw new RuntimeException("Goal does not belong to user: " + userId);
        }
        
        goal.setTitle(goalDetails.getTitle());
        goal.setDescription(goalDetails.getDescription());
        
        if (goalDetails.getCategory() != null) {
            goal.setCategory(goalDetails.getCategory());
        }
        if (goalDetails.getStatus() != null) {
            goal.setStatus(goalDetails.getStatus());
        }
        
        return goalRepository.save(goal);
    }

    public void deleteGoal(Long goalId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Goal goal = getGoalById(goalId);
        if (goal == null) {
            throw new RuntimeException("Goal not found: " + goalId);
        }
        
        // Verifica se a goal pertence ao usuário
        if (!user.getGoals().contains(goal)) {
            throw new RuntimeException("Goal does not belong to user: " + userId);
        }
        
        // Remove a goal do usuário
        user.getGoals().remove(goal);
        userRepository.save(user);
        
        // Deleta a goal do banco
        goalRepository.delete(goal);
    }
}
