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

    public static final String SUB = "sub";
    public static final String PASSWORD = "123";
    public static final String PREFERRED_USERNAME = "preferred_username";
    public static final String REALM_ACCESS = "realm_access";
    public static final String ROLES = "roles";
    public static final String ROLE_PREFIX = "ROLE_";

    public static RequestPostProcessor jwtWithRole(String userName, String... roles) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> {
                    jwt.claim(SUB, PASSWORD);
                    jwt.claim(PREFERRED_USERNAME, userName);
                    jwt.claim(REALM_ACCESS, Map.of(ROLES, List.of(roles)));
                })
                .authorities(Stream.of(roles)
                        .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
                        .collect(Collectors.toList()));
    }
}
