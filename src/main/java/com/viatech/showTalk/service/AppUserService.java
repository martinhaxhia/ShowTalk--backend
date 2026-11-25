package com.viatech.showTalk.service;

import com.viatech.showTalk.DTO.AppUserRegisterRequest;
import com.viatech.showTalk.DTO.AppUserResponse;

import com.viatech.showTalk.DTO.LoginRequest;
import com.viatech.showTalk.DTO.LoginResponse;
import com.viatech.showTalk.entites.AppUser;
import com.viatech.showTalk.exceptions.DuplicateUserException;
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
    public AppUserResponse registerUser(AppUserRegisterRequest request) {


        if (appUserRepository.findByEmail(request.email()).isPresent()) {
            throw new DuplicateUserException("Email already registered");
        }

        AppUser userEntity = new AppUser();
        userEntity.setUsername(request.username());
        userEntity.setEmail(request.email());
        userEntity.setPassword(passwordEncoder.encode(request.password()));
        userEntity.setRole(Role.USER);

        AppUser savedUser = appUserRepository.save(userEntity);

        return new AppUserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole().name()
        );


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









    //I will learn you this when we are at the office cause this is the way its best to be done
    //its okay for now but we will change it

}

