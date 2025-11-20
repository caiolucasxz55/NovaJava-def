package com.fiap.nova.dto;

public record TokenResponse(
    String token,
    Long id,
    String name,
    String email
) {}