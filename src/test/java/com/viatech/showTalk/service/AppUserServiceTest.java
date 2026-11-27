package com.viatech.showTalk.service;


import com.viatech.showTalk.DTO.AppUserRegisterRequest;
import com.viatech.showTalk.DTO.AppUserResponse;
import com.viatech.showTalk.DTO.LoginRequest;
import com.viatech.showTalk.DTO.LoginResponse;
import com.viatech.showTalk.entites.AppUser;
import com.viatech.showTalk.entites.Role;
import com.viatech.showTalk.exceptions.DuplicateUserException;
import com.viatech.showTalk.repositories.AppUserRepository;
import com.viatech.showTalk.utils.JwtUtil;
import com.viatech.showTalk.utils.mapper.AppUserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppUserServiceTest {


    @Mock
    //mock is used to make a copy of the class so we can use the copy for running test
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AppUserMapper mapper;

    @InjectMocks
    private AppUserService appUserService;



    private AppUserRegisterRequest appUserRegisterRequest;
    private LoginRequest loginRequest;
    private AppUser appUser;

    @BeforeEach
    //runs before any test.
    //this method is used to make some test objects  that we will use in the test cases
    public void setUp() {
        appUserRegisterRequest = new AppUserRegisterRequest(
                "test",
                "test@test.com",
                "test123"

        );

         loginRequest = new LoginRequest(
                "test@test.com",
                "test123"
        );

        appUser = new AppUser();
        appUser.setId(1L);
        appUser.setUsername("test");
        appUser.setEmail("test@test.com");
        appUser.setPassword("encodedPass");
        appUser.setRole(Role.USER);

    }


    @Test
    public void testRegisterSuccess() {
        AppUserResponse expectedUser = new AppUserResponse(
                1L, "test", "test@test.com", "USER"
        );

        when(appUserRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.empty());
        when(appUserRepository.findByUsername("test"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("test123"))
                .thenReturn("encodedPass");
        when(appUserRepository.save(any(AppUser.class)))
                .thenReturn(appUser);
        when(jwtUtil.generateToken(appUser))
                .thenReturn("mockToken");
        when(mapper.userToResponse(appUser))
                .thenReturn(expectedUser);

        LoginResponse response = appUserService.registerUser(appUserRegisterRequest);

        assertEquals("mockToken", response.accessToken());
        assertEquals(expectedUser, response.user());
    }



    @Test
    public void testRegisterDuplicateEmail() {
        when(appUserRepository.findByEmail("test@test.com")).thenReturn(Optional.of(appUser));
        assertThrows(DuplicateUserException.class, () -> appUserService.registerUser(appUserRegisterRequest));
    }

    @Test
    public void testRegisterDuplicateUsername() {
        when(appUserRepository.findByUsername("test")).thenReturn(Optional.of(appUser));
        assertThrows(DuplicateUserException.class, () -> appUserService.registerUser(appUserRegisterRequest));
    }


    @Test
    public void testLoginSuccess(){
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken( appUser, null, appUser.getAuthorities()));

        AppUserResponse expectedUser = new AppUserResponse(
                1L, "test", "test@test.com", "USER"
        );

        when(mapper.userToResponse(appUser)).thenReturn(expectedUser);
        when(jwtUtil.generateToken(this.appUser)).thenReturn("mockToken");

        LoginResponse response = appUserService.login(loginRequest);

        assertNotNull(response);
        assertEquals(expectedUser, response.user());
        assertEquals("mockToken", response.accessToken());



    }

//this here tests for both invalid email and password, but i shall test those in different parts.
    @Test
    public void testLoginWithInvaliCredentials() {

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> appUserService.login(loginRequest));
        verifyNoInteractions(jwtUtil);

    }







}
