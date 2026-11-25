package com.viatech.showTalk.utils;

import com.viatech.showTalk.entites.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;


@Component
public class JwtUtil {



    private final JwtEncoder encoder;

    public JwtUtil(JwtEncoder encoder){

        this.encoder = encoder;
    }

    public String generateToken(AppUser user) {
        Instant now = Instant.now();

        String scope = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(24, ChronoUnit.HOURS))
                .subject(user.getUsername())
                .claim("email", user.getEmail())
                .claim("userId", user.getId())
                .claim("scope", scope)
                .build();

        return this.encoder.encode(
                JwtEncoderParameters.from(claims)
        ).getTokenValue();
    }


}