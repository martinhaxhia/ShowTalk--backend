package com.viatech.showTalk.controller;


import com.viatech.showTalk.DTO.AppUserResponse;
import com.viatech.showTalk.service.AppUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class AppUserController {


    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public AppUserResponse getUser(@AuthenticationPrincipal Jwt jwt) {

        Number userIdClaim = jwt.getClaim("userId");
        Long userId = userIdClaim.longValue();

        return appUserService.getCurrentUser(userId);
    }
}
