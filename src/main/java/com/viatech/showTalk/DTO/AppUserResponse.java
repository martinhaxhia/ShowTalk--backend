package com.viatech.showTalk.DTO;

public record AppUserResponse(
        Long id,
        String username,
        String email,
        String role
) {}