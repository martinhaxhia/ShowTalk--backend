package com.viatech.showTalk.service;

import com.viatech.showTalk.DTO.AppUserRegisterRequest;
import com.viatech.showTalk.DTO.AppUserResponse;

import com.viatech.showTalk.DTO.LoginRequest;
import com.viatech.showTalk.DTO.LoginResponse;
import com.viatech.showTalk.entites.AppUser;
import com.viatech.showTalk.exceptions.DuplicateUserException;
import com.viatech.showTalk.exceptions.UserNotFoundException;
import com.viatech.showTalk.repositories.AppUserRepository;
import com.viatech.showTalk.utils.mapper.AppUserMapper;
import org.springframework.security.authentication.AuthenticationManager;
import com.viatech.showTalk.utils.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.viatech.showTalk.entites.Role;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final AppUserMapper appUserMapper;




    public AppUserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder ,
                          JwtUtil jwtUtil, AuthenticationManager authenticationManager, AppUserMapper appUserMapper) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;

        this.authenticationManager = authenticationManager;
        this.appUserMapper = appUserMapper;
    }

    @Transactional
    public LoginResponse registerUser(AppUserRegisterRequest request) {


        if (appUserRepository.findByEmail(request.email()).isPresent()) {
            throw new DuplicateUserException("Email already registered");
        }

        if(appUserRepository.findByUsername(request.username()).isPresent()) {
            throw new DuplicateUserException("Username already registered");
        }




        AppUser appUser = new AppUser();
        appUser.setUsername( request.username() );
        appUser.setEmail( request.email() );
        appUser.setPassword(passwordEncoder.encode(request.password()));
        appUser.setRole(Role.USER);
        AppUser savedUser = appUserRepository.save(appUser);
        String accessToken = jwtUtil.generateToken(savedUser);

        AppUserResponse userResponse = appUserMapper.userToResponse(savedUser);

        return new LoginResponse(accessToken, userResponse);



    }


    public LoginResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        AppUser user = (AppUser) authentication.getPrincipal();
        String accessToken = jwtUtil.generateToken(user);

        AppUserResponse userResponse = appUserMapper.userToResponse(user);

        return new LoginResponse(accessToken, userResponse);
    }


    public AppUserResponse getCurrentUser(Long userId){

        return appUserMapper.userToResponse(
                appUserRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("User not found"))
        );
    }






}

