package fr.perretpy.myBoxes.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
public class CorsConfig implements Customizer<CorsConfigurer<HttpSecurity>> {
    @Override
    public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
        httpSecurityCorsConfigurer.configurationSource(request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.addAllowedOriginPattern("*");
            configuration.addAllowedMethod("GET");
            configuration.addAllowedMethod("POST");
            configuration.addAllowedMethod("PUT");
            configuration.addAllowedMethod("DELETE");
            configuration.addAllowedHeader("*");
            configuration.setAllowCredentials(true);

            return configuration;
        });
    }
}
