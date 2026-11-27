package com.viatech.showTalk.service;



import com.viatech.showTalk.DTO.LoginRequest;
import com.viatech.showTalk.DTO.LoginResponse;
import com.viatech.showTalk.DTO.RegisterRequest;
import com.viatech.showTalk.DTO.RegisterResponse;

import com.viatech.showTalk.entites.Role;
import com.viatech.showTalk.entites.User;
import com.viatech.showTalk.exceptions.DuplicateUserException;

import com.viatech.showTalk.repositories.UserRepository;
import com.viatech.showTalk.utils.JwtUtil;

import com.viatech.showTalk.utils.mapper.UserMapper;
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
public class UserServiceTest {


    @Mock
    //mock is used to make a copy of the class so we can use the copy for running test
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserService userService;



    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    //runs before any test.
    //this method is used to make some test objects  that we will use in the test cases
    public void setUp() {
        registerRequest = new RegisterRequest(

                "test",
                "test@test.com",
                "test123"

        );

        loginRequest = new LoginRequest(

                "test@test.com",
                "test123"
        );

        user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setEmail("test@test.com");
        user.setPassword("encodedPass");
        user.setRole(Role.USER);

    }


    @Test
    public void testRegisterSuccess() {
        RegisterResponse expectedUser = new  RegisterResponse(
                1L, "test", "test@test.com", "USER"
        );

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.empty());
        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("test123"))
                .thenReturn("encodedPass");
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        when(jwtUtil.generateToken(user))
                .thenReturn("mockToken");
        when(mapper.userToResponse(user))
                .thenReturn(expectedUser);

        LoginResponse response = userService.registerUser(registerRequest);

        assertEquals("mockToken", response.accessToken());
        assertEquals(expectedUser, response.user());
    }



    @Test
    public void testRegisterDuplicateEmail() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        assertThrows(DuplicateUserException.class, () -> userService.registerUser(registerRequest));
    }

    @Test
    public void testRegisterDuplicateUsername() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        assertThrows(DuplicateUserException.class, () -> userService.registerUser(registerRequest));
    }


    @Test
    public void testLoginSuccess(){
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken( user, null, user.getAuthorities()));

        RegisterResponse expectedUser = new   RegisterResponse(
                1L, "test", "test@test.com", "USER"
        );

        when(mapper.userToResponse(user)).thenReturn(expectedUser);
        when(jwtUtil.generateToken(this.user)).thenReturn("mockToken");

        LoginResponse response = userService.login(loginRequest);

        assertNotNull(response);
        assertEquals(expectedUser, response.user());
        assertEquals("mockToken", response.accessToken());



    }

    //this here tests for both invalid email and password, but i shall test those in different parts.
    @Test
    public void testLoginWithInvaliCredentials() {

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> userService.login(loginRequest));
        verifyNoInteractions(jwtUtil);

    }







}
