package com.fiap.nova.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.EntityModel;

@Entity
@Table(name = "NOVA_SKILLS") // Nome de tabela mais limpo
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "skill_seq")
    @SequenceGenerator(name = "skill_seq", sequenceName = "SEQ_SKILLS", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Skill name is required")
    @Column(name = "nome", nullable = false, length = 100, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 20)
    private SkillType type; 


    public EntityModel<Skill> toEntityModel() {
        return EntityModel.of(this);
    }
}