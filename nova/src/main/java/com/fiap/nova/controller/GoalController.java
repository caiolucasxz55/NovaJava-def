package com.fiap.nova.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.fiap.nova.model.Goal;
import com.fiap.nova.service.GoalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/goals")
@Tag(name = "Goals", description = "User goals management")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping
    @Operation(summary = "List all goals with pagination", description = "Returns paginated list of goals for a specific user")
    public PagedModel<EntityModel<Goal>> getAll(
            @RequestParam(required = false) Long userId, 
            @PageableDefault(size = 10, sort = "title") Pageable pageable,
            PagedResourcesAssembler<Goal> assembler) {
        
        log.info("Listing paginated goals for userId: {} - page: {}, size: {}", userId, pageable.getPageNumber(), pageable.getPageSize());
        
        var page = goalService.listGoalsByUser(userId, pageable);
        return assembler.toModel(page, Goal::toEntityModel);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new goal", description = "Creates a new goal linked to a user")
    public EntityModel<Goal> createGoal(@RequestBody @Valid Goal goal, @RequestParam Long userId) {
        log.info("Creating goal: {} for userId: {}", goal, userId);
        var newGoal = goalService.createGoal(goal, userId);
        return newGoal.toEntityModel();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get goal by ID", description = "Returns a specific goal with HATEOAS links")
    public EntityModel<Goal> getById(@PathVariable Long id) {
        log.info("Getting goal with id: {}", id);
        var goal = goalService.getGoalById(id);
        if (goal == null) {
            throw new RuntimeException("Goal not found");
        }
        return goal.toEntityModel();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update goal", description = "Updates an existing goal for a specific user")
    public EntityModel<Goal> updateGoal(@PathVariable Long id, @RequestParam Long userId, @RequestBody @Valid Goal goal) {
        log.info("Updating goal with id: {} for userId: {}", id, userId);
        var updatedGoal = goalService.updateGoal(id, userId, goal);
        return updatedGoal.toEntityModel();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) 
    @Operation(summary = "Delete goal", description = "Deletes a goal by ID for a specific user")
    public void deleteGoal(@PathVariable Long id, @RequestParam Long userId) {
        log.info("Deleting goal with id: {} for userId: {}", id, userId);
        goalService.deleteGoal(id, userId);
    }
}