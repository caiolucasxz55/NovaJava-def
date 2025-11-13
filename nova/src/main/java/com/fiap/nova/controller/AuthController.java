package com.fiap.nova.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.fiap.nova.model.User;
import com.fiap.nova.service.TokenService;
import com.fiap.nova.service.UserService;

@RestController
public class AuthController {

    private final TokenService tokenService;
    private final UserService userService;
    
    public AuthController(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    public record TokenResponse(String token, String username) {}

    @PostMapping("/login")
    public TokenResponse login(Authentication authentication) {
        String username = authentication.getName();
        String token = tokenService.generateToken(username);
        return new TokenResponse(token, username);
    }

    @PostMapping("/register")
    public TokenResponse register(@RequestBody User user) {
        // Cria o usuário no banco
        User savedUser = userService.createUser(user);
        
        // Gera token automaticamente (login automático após cadastro)
        String token = tokenService.generateToken(savedUser.getEmail());
        
        return new TokenResponse(token, savedUser.getEmail());
    }
}

