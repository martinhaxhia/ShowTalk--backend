package com.viatech.showTalk.controller;


import com.viatech.showTalk.DTO.RegisterRequest;
import com.viatech.showTalk.DTO.RegisterResponse;
import com.viatech.showTalk.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public RegisterResponse getUser(@AuthenticationPrincipal Jwt jwt) {

        Number userIdClaim = jwt.getClaim("userId");
        Long userId = userIdClaim.longValue();

        return userService.getCurrentUser(userId);
    }
}
