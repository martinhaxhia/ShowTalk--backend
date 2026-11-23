package com.viatech.showTalk.security;


import com.viatech.showTalk.entites.AppUser;
import com.viatech.showTalk.repositories.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    //Stores the repository used to look up accounts
    private final AppUserRepository userRepository;

    @Override
    //Takes a username to look up and returns the account in Spring Security UserDetails format.
    public UserDetails loadUserByUsername(String username) {
        //Searches for an account with that username
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));

        //Uses Spring Security built-in User class to create the result from  AppUser
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword_hash())
                .roles(user.getRole().name())
                .build(); // there is no need for this you can implement UserDetails interface in the AppUser table and implement the methods
    }
}
