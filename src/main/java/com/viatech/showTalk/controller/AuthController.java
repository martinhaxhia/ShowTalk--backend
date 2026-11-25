package com.viatech.showTalk.controller;


import com.viatech.showTalk.DTO.AppUserRegisterRequest;
import com.viatech.showTalk.DTO.AppUserResponse;
import com.viatech.showTalk.DTO.LoginRequest;
import com.viatech.showTalk.DTO.LoginResponse;
import com.viatech.showTalk.service.AppUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AppUserService appUserService;

    public AuthController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping("/register")
    public ResponseEntity<AppUserResponse> register(@Valid @RequestBody AppUserRegisterRequest userRegisterRequest) {
        AppUserResponse response = appUserService.registerUser(userRegisterRequest);
        return  ResponseEntity.ok(response);
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = appUserService.login(request);
        return ResponseEntity.ok(response);
    }
}
