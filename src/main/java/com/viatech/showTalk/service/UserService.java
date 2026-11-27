package com.viatech.showTalk.service;

import com.viatech.showTalk.DTO.RegisterRequest;
import com.viatech.showTalk.DTO.RegisterResponse;

import com.viatech.showTalk.DTO.LoginRequest;
import com.viatech.showTalk.DTO.LoginResponse;
import com.viatech.showTalk.entites.User;
import com.viatech.showTalk.exceptions.DuplicateUserException;
import com.viatech.showTalk.exceptions.UserNotFoundException;
import com.viatech.showTalk.repositories.UserRepository;
import com.viatech.showTalk.utils.mapper.UserMapper;
import org.springframework.security.authentication.AuthenticationManager;
import com.viatech.showTalk.utils.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.viatech.showTalk.entites.Role;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    private final UserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserMapper appUserMapper;




    public UserService(UserRepository appUserRepository, PasswordEncoder passwordEncoder ,
                       JwtUtil jwtUtil, AuthenticationManager authenticationManager, UserMapper appUserMapper) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;

        this.authenticationManager = authenticationManager;
        this.appUserMapper = appUserMapper;
    }

    @Transactional
    public LoginResponse registerUser(RegisterRequest request) {


        if (appUserRepository.findByEmail(request.email()).isPresent()) {
            throw new DuplicateUserException("Email already registered");
        }

        if (appUserRepository.findByUsername(request.username()).isPresent()) {
            throw new DuplicateUserException("Username already registered");
        }

        User appUser = new User();
        appUser.setUsername( request.username() );
        appUser.setEmail( request.email() );
        appUser.setPassword(passwordEncoder.encode(request.password()));
        appUser.setRole(Role.USER);

        User savedUser = appUserRepository.save(appUser);
        String accessToken = jwtUtil.generateToken(savedUser);
       RegisterResponse userResponse = appUserMapper.userToResponse(savedUser);

        return new LoginResponse(accessToken, userResponse);


    }


    public LoginResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = (User) authentication.getPrincipal();
        String accessToken = jwtUtil.generateToken(user);

        RegisterResponse userResponse = appUserMapper.userToResponse(user);

        return new LoginResponse(accessToken, userResponse);
    }

    public RegisterResponse getCurrentUser(Long userId){

        return appUserMapper.userToResponse(
                appUserRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("User not found"))
        );
    }










}

