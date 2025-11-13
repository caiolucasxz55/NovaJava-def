package com.fiap.nova.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFilters {
    
    private String nome;
    private String email;
    private String professionalGoal;
}
