package com.fiap.nova.model;

import java.util.Collection;
import java.util.List;

import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.fiap.nova.controller.UserController;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor 
@Builder
@Entity
@Table(name = "NOVA_USUARIO")
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
	@SequenceGenerator(name = "usuario_seq", sequenceName = "SEQ_USUARIO_NOVA", allocationSize = 1)
	@Column(name = "nova_id_usuario")
	private Long id;

	@NotBlank(message = "Name is required")
	@Column(name = "nova_nome_usuario", nullable = false, length = 200)
	private String name;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email")
	@Column(name = "nova_email_usuario", nullable = false, length = 200, unique = true)
	private String email;

	@NotBlank(message = "Password is required")
	@Column(name = "nova_senha_usuario", nullable = false, length = 100)
	private String password;

	@NotBlank(message = "Professional goal is required")
	@Column(name = "nova_objetivo_prof_usuario", nullable = false, length = 255)
	private String professionalGoal;

	@Transient
	private String role;

	@ManyToMany
	@JoinTable(
		name = "NOVA_USUARIO_METAS",
		joinColumns = @JoinColumn(name = "nova_id_usuario"),
		inverseJoinColumns = @JoinColumn(name = "nova_id_meta")
	)
	private List<Goal> goals;

	@ManyToMany
	@JoinTable(
		name = "NOVA_USUARIO_SKILLS_GOALS",
		joinColumns = @JoinColumn(name = "nova_id_usuario"),
		inverseJoinColumns = @JoinColumn(name = "nova_id_habilidade")
	)
	private List<Skill> skills;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_USER"));
	}

	@Override
	public String getUsername() {
		return email;
	}

	public EntityModel<User> toEntityModel() {
        return EntityModel.of(this)
                .add(linkTo(methodOn(UserController.class)
                        .getById(this.id)).withSelfRel());
    }
}
