package com.viatech.showTalk.service;

import com.viatech.showTalk.DTO.AppUserRegisterRequest;
import com.viatech.showTalk.DTO.AppUserResponse;

import com.viatech.showTalk.entites.AppUser;
import com.viatech.showTalk.repositories.AppUserRepository;
import com.viatech.showTalk.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import com.viatech.showTalk.entites.Role;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;


    public AppUserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;

    }

    @Transactional
    public AppUserResponse registerUser(AppUserRegisterRequest request) {

        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (request.getEmail() == null || !request.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (appUserRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        AppUser userEntity = new AppUser();
        userEntity.setUsername(request.getUsername());
        userEntity.setEmail(request.getEmail());
        userEntity.setPassword_hash(passwordEncoder.encode(request.getPassword()));
        userEntity.setRole(Role.USER);
        appUserRepository.save(userEntity);

        return appUserToAppUserResponse(userEntity);
    }


    public AppUserResponse registerModerator(AppUserRegisterRequest request) {


        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (request.getEmail() == null || !request.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (appUserRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        AppUser userEntity = new AppUser();
        userEntity.setUsername(request.getUsername());
        userEntity.setEmail(request.getEmail());
        userEntity.setPassword_hash(passwordEncoder.encode(request.getPassword()));
        userEntity.setRole(Role.MODERATOR);
        appUserRepository.save(userEntity);

        return appUserToAppUserResponse(userEntity);
    }






    public AppUserResponse appUserToAppUserResponse(AppUser appUser) {
        AppUserResponse response = new AppUserResponse();

        response.setId(appUser.getId());
        response.setUsername(appUser.getUsername());
        response.setEmail(appUser.getEmail());
        response.setRole(appUser.getRole().toString());


        return response;
    }

}

