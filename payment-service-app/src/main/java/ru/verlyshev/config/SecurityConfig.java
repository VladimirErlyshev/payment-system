package ru.verlyshev.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import ru.verlyshev.security.KeyCloakRealmRoleConverter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    public static final String PAYMENTS_API_V1_PATTERN = "/api/v1/payments/**";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        final var jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeyCloakRealmRoleConverter());

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PAYMENTS_API_V1_PATTERN).authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(
                    jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)
                )).build();
    }
}
