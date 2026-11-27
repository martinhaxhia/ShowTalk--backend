package com.viatech.showTalk.controller;


import com.viatech.showTalk.DTO.RegisterRequest;
import com.viatech.showTalk.DTO.RegisterResponse;
import com.viatech.showTalk.DTO.LoginRequest;
import com.viatech.showTalk.DTO.LoginResponse;
import com.viatech.showTalk.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService appUserService;

    public AuthController(UserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = appUserService.registerUser(request);
        return  ResponseEntity.ok(response);
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = appUserService.login(request);
        return ResponseEntity.ok(response);
    }
}
