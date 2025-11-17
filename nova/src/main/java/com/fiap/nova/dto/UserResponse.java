package com.fiap.nova.dto;

import com.fiap.nova.model.User;

public record UserResponse(
    Long id,
    String name,
    String email,
    String professionalGoal,
    String role
) {
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getProfessionalGoal(),
            user.getRole()
        );
    }
}
