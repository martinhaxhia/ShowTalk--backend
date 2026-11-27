package com.viatech.showTalk.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank @Size(min = 3, max = 30) String username,
        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Email must be valid") String email,
        @NotBlank @Size(min = 8, max = 50) String password
) {}