package com.viatech.showTalk.controller;


import com.viatech.showTalk.DTO.AppUserRegisterRequest;
import com.viatech.showTalk.DTO.AppUserResponse;
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
        return new ResponseEntity<>(appUserService.registerUser(userRegisterRequest), HttpStatus.CREATED);
    }

    @PostMapping("/register-moderator")
    public ResponseEntity<AppUserResponse> registerModerator(@Valid @RequestBody AppUserRegisterRequest userRegisterRequest) {
        return new ResponseEntity<>(appUserService.registerModerator(userRegisterRequest), HttpStatus.CREATED);
    }
}
