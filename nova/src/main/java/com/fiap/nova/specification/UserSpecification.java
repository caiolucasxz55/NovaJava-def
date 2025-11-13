package com.fiap.nova.specification;

import org.springframework.data.jpa.domain.Specification;

import com.fiap.nova.dto.UserFilters;
import com.fiap.nova.model.User;

public class UserSpecification {

    public static Specification<User> build(UserFilters filters) {
        Specification<User> spec = Specification.anyOf();

        if (filters == null) {
            return spec;
        }

        if (filters.getNome() != null && !filters.getNome().isBlank()) {
            spec = spec.and((root, query, cb) ->
                cb.like(cb.upper(root.get("nome")), "%" + filters.getNome().toUpperCase() + "%")
            );
        }

        if (filters.getEmail() != null && !filters.getEmail().isBlank()) {
            spec = spec.and((root, query, cb) ->
                cb.like(cb.upper(root.get("email")), "%" + filters.getEmail().toUpperCase() + "%")
            );
        }

        if (filters.getProfessionalGoal() != null && !filters.getProfessionalGoal().isBlank()) {
            spec = spec.and((root, query, cb) ->
                cb.like(cb.upper(root.get("professionalGoal")), "%" + filters.getProfessionalGoal().toUpperCase() + "%")
            );
        }

        return spec;
    }
}
