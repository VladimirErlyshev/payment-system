package ru.verlyshev.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
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
        Map<String, Object> realmAccess = jwt.getClaim(REALM_ROLE_CLAIM_NAME);
        if (realmAccess == null || realmAccess.get(ROLE_PARAM_NAME) == null) {
            return Collections.emptyList();
        }

        Object rolesObject = realmAccess.get(ROLE_PARAM_NAME);

        Collection<String> roles = new ArrayList<>();

        if (rolesObject instanceof Collection) {
            for (Object r : (Collection<?>) rolesObject) {
                if (r instanceof String) {
                    roles.add((String) r);
                }
            }
        } else if (rolesObject instanceof String) {
            roles.add((String) rolesObject);
        }

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role.toUpperCase()))
                .collect(Collectors.toSet());
    }
}
