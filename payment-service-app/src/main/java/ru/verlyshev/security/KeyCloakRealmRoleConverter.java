package ru.verlyshev.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class KeyCloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    public static final String REALM_ROLE_CLAIM_NAME = "realm_access";
    public static final String ROLE_PARAM_NAME = "roles";
    public static final String ROLE_PREFIX = "ROLE_";

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        final Map<String, Object> realmAccess = jwt.getClaim(REALM_ROLE_CLAIM_NAME);

        if (realmAccess == null || realmAccess.get(ROLE_PARAM_NAME) == null) {
            return Collections.emptyList();
        }

        final Collection<String> roles = (Collection<String>) realmAccess.get(ROLE_PARAM_NAME);

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role.toUpperCase()))
                .collect(Collectors.toSet());
    }
}
