package com.viatech.showTalk.config;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.viatech.showTalk.repositories.AppUserRepository;
import com.viatech.showTalk.utils.Jwks;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity //allows us to use @PreAuthorize
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final AppUserRepository appUserRepository;

    @Value("${app.security.jwt.key-dir}")
    private String keyDir;

    @Value("${app.security.jwt.generate-if-missing}")
    private boolean generateKeyIfMissing;


    @Bean
    public AuthenticationManager authManager(
            UserDetailsService userDetailsService) {

        //takes an account and checks the submitted credentials against it
        var authProvider =
                new DaoAuthenticationProvider(userDetailsService);

        //gives the provider the BCrypt
        authProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(authProvider);
    }


    @Bean
    public UserDetailsService userDetailsService() {
        return email -> appUserRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User Not Found"));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(authorize -> authorize
                //are accessed  without authentication
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",

                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**"
                        ).permitAll()
                                //are accessed only if authenticated
                        .anyRequest().authenticated()
                )

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                //disabled because csrf is stateful,  and we do not save the tokens in a state
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // instead of the JWTFilter , doFilterInternal which was called once per req
                //it extracts bearer token ,read the header authorization and removed barer to get only the token
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*"
        ));
        config.setAllowCredentials(true);
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    // generates the key from the directory
    public RSAKey rsaKey() {
        RSAKey rsaKey = Jwks.load(Path.of(keyDir), generateKeyIfMissing);

        return rsaKey;
    }
    @Bean
    public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey) {
        //is a set of standardized rep of cryptographic keys
        JWKSet jwkSet = new JWKSet(rsaKey);
        //jwkSelector -> filters the keys by some criteria like algorithm , key use etc.
        //securityContext -> provides request or security context details

        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

//produces a signed token after getting the claims.
    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }


    //decodes the jwt and verifies its signature and claims

    @Bean
    public JwtDecoder jwtDecoder(RSAKey rsaKey) throws JOSEException {
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withPublicKey(rsaKey.toRSAPublicKey())
                .build();

        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("self"));
        return decoder;
    }


}