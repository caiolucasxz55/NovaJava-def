package com.fiap.nova.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fiap.nova.dto.UserUpdateRequest;
import com.fiap.nova.filters.UserFilters;
import com.fiap.nova.model.Skill;
import com.fiap.nova.model.SkillType;
import com.fiap.nova.model.User;
import com.fiap.nova.repository.SkillRepository;
import com.fiap.nova.repository.UserRepository;
import com.fiap.nova.specification.UserSpecification;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SkillRepository skillRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, SkillRepository skillRepository){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.skillRepository = skillRepository;
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("ROLE_USER");
        }
        return userRepository.save(user);
    }

    public Page<User> findAll(Pageable pageable, UserFilters filters) {
        Specification<User> spec = UserSpecification.build(filters);
        return userRepository.findAll(spec, pageable);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElse(null);
    }

    public User updateUser(Long id, UserUpdateRequest request) {
        User user = findById(id);
        user.setName(request.name());
        user.setProfessionalGoal(request.professionalGoal());      
        if (request.skills() != null) {
            List<Skill> newSkills = new ArrayList<>();
            for (String skillName : request.skills()) {
                Optional<Skill> existingSkill = skillRepository.findByNameIgnoreCase(skillName);
                Skill skill;
                if (existingSkill.isPresent()) {
                    skill = existingSkill.get();
                } else {
                    skill = Skill.builder()
                            .name(skillName)
                            .type(SkillType.HARD)
                            .build();
                    skill = skillRepository.save(skill);
                }
                newSkills.add(skill);
            }
            user.setSkills(newSkills);
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }
}

