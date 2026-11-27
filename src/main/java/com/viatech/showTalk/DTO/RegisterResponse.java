package com.viatech.showTalk.DTO;

public record RegisterResponse(
        Long id,
        String username,
        String email,
        String role
) {}