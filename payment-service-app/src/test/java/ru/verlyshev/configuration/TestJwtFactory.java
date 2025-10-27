package ru.verlyshev.configuration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestJwtFactory {

    public static RequestPostProcessor jwtWithRole(String userName, String... roles) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> {
                    jwt.claim("sub", "123");
                    jwt.claim("preferred_username", userName);
                    jwt.claim("realm_access", Map.of("roles", List.of(roles)));
                })
                .authorities(Stream.of(roles)
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList()));
    }
}
