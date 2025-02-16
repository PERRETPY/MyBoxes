package fr.perretpy.MyBoxes.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

@Configuration
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;


    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        // Récupérer les rôles dans realm_access
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            List<String> roles = (List<String>) realmAccess.get("roles");
            authorities.addAll(roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .toList());
        }

        // Récupérer les rôles dans resource_access
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null && resourceAccess.containsKey(clientId)) {
            Map<String, Object> clientRoles = (Map<String, Object>) resourceAccess.get(clientId);
            if (clientRoles.containsKey("roles")) {
                List<String> roles = (List<String>) clientRoles.get("roles");
                authorities.addAll(roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .toList());
            }
        }

        return authorities;
    }
}
