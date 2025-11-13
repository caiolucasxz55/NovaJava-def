package com.fiap.nova.service;

import java.sql.Time;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fiap.nova.dto.UserFilters;
import com.fiap.nova.model.User;
import com.fiap.nova.repository.UserRepository;
import com.fiap.nova.specification.UserSpecification;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {

        // Criptografa a senha com BCrypt
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Salva no banco
        return userRepository.save(user);
    }

    public Page<User> findAll(Pageable pageable, UserFilters filters) {
        Specification<User> spec = UserSpecification.build(filters);
        return userRepository.findAll(spec, pageable);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}

