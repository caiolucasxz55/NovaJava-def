package com.fiap.nova.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.fiap.nova.model.Skill;
import com.fiap.nova.service.SkillService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/skills")
@Tag(name = "Skills", description = "User skills management")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    @Operation(summary = "List skills with pagination", description = "Returns paginated list of skills for a specific user or all skills if no userId provided")
    public PagedModel<EntityModel<Skill>> getAll(
            @RequestParam(required = false) Long userId,
            @PageableDefault(size = 10, sort = "type") Pageable pageable,
            PagedResourcesAssembler<Skill> assembler) {
        log.info("Listing paginated skills for userId: {} - page: {}, size: {}", userId, pageable.getPageNumber(), pageable.getPageSize());
        
        var page = userId != null 
            ? skillService.listSkillsByUser(userId, pageable)
            : skillService.listAllPaginated(pageable);
            
        return assembler.toModel(page, Skill::toEntityModel);
    }

    @GetMapping("/all")
    @Operation(summary = "List all skills without pagination", description = "Returns complete list of skills")
    public List<Skill> listAll() {
        log.info("Listing all skills");
        return skillService.listAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new skill", description = "Creates a new skill for a specific user")
    public Skill createSkill(@RequestBody @Valid Skill skill, @RequestParam Long userId) {
        log.info("Creating skill: {} for userId: {}", skill, userId);
        return skillService.createSkill(skill, userId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get skill by ID", description = "Returns a specific skill with HATEOAS links")
    public EntityModel<Skill> getById(@PathVariable Long id) {
        log.info("Getting skill with id: {}", id);
        var skill = skillService.getSkillById(id);
        return skill.toEntityModel();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update skill", description = "Updates an existing skill for a specific user")
    public EntityModel<Skill> updateSkill(@PathVariable Long id, @RequestParam Long userId, @RequestBody @Valid Skill skill) {
        log.info("Updating skill with id: {} for userId: {}", id, userId);
        var updatedSkill = skillService.updateSkill(id, userId, skill);
        return updatedSkill.toEntityModel();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete skill", description = "Removes a skill from a specific user")
    public void deleteSkill(@PathVariable Long id, @RequestParam Long userId) {
        log.info("Deleting skill with id: {} for userId: {}", id, userId);
        skillService.deleteSkill(id, userId);
    }
}
