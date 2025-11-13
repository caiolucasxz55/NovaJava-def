package com.fiap.nova.model;

import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.fiap.nova.controller.UserController;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor 
@Builder
@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nome;

	private String email;

	private String password;

	private String professionalGoal;

	public EntityModel<User> toEntityModel() {
        return EntityModel.of(this)
                .add(linkTo(methodOn(UserController.class)
                        .getById(this.id)).withSelfRel());
    }
    
}
