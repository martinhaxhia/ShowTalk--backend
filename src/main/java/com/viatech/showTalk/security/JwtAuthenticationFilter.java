package com.viatech.showTalk.security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override

    //Allows servlet-processing and input/output errors to spread to the caller
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            //checks if token is valid
            if (jwtUtil.isTokenValid(token)) {
                //reads the JWT subject
                String username = jwtUtil.extractUsername(token);

                //looks up the account using the username
                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                //creates the object that spring security uses to represent the authenticated user
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                //stores the authentication in the current security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        //passes the request and response to the next filter or to the application
        chain.doFilter(request, response);
    }

    //As i have told to Elisa also this isnt the standart way check oauth2Server but for Elisa i let it be cause you didnt have time
    //but here you need to do it with oauth2Server  i will paste you a document you can read it out and learn the security flow
}
