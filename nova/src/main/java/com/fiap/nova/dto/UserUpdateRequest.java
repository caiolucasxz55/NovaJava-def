package com.fiap.nova.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @NotBlank(message = "Name is required")
    @Size(max = 200)
    String name,
    
    @NotBlank(message = "Professional goal is required")
    @Size(max = 255)
    String professionalGoal,
    
    List<String> skills
) {
}
