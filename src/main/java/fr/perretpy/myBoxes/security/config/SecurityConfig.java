package fr.perretpy.myBoxes.security.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final CorsConfig corsConfig;

    private final AuthorizeRequestConfig authorizeRequestConfig;

    private final KeycloakJwtAuthenticationConverter keycloakJwtAuthenticationConverter;

    @Autowired
    public SecurityConfig(AuthorizeRequestConfig authorizeRequestConfig,
                          KeycloakJwtAuthenticationConverter keycloakJwtAuthenticationConverter,
                          CorsConfig corsConfig) {
        this.authorizeRequestConfig = authorizeRequestConfig;
        this.keycloakJwtAuthenticationConverter = keycloakJwtAuthenticationConverter;
        this.corsConfig = corsConfig;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(corsConfig)
                .authorizeHttpRequests(authorizeRequestConfig)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(keycloakJwtAuthenticationConverter);
        return converter;
    }
}
