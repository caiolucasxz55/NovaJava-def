package com.fiap.nova.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import com.fiap.nova.dto.TokenResponse;
import com.fiap.nova.model.User;

@Service
public class TokenService {

    private final JwtEncoder encoder;

    public TokenService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public TokenResponse generateToken(Authentication authentication) {
        Instant now = Instant.now();

        User user = (User) authentication.getPrincipal();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("nova-api")
                .issuedAt(now)
                .expiresAt(now.plus(24, ChronoUnit.HOURS))
                .subject(user.getEmail()) 
                .claim("id", user.getId()) 
                .build();
        
        String token = this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new TokenResponse(
            token,
            user.getId(),
            user.getName(),            
            user.getEmail()
        );
    }
}