package com.viatech.showTalk.DTO;

public record LoginResponse(
        String accessToken,
        RegisterResponse user
) {}
