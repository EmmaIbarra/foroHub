package com.aluracursos.foro_hub.infra.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // Desactiva CSRF si estás usando Insomnia
            .authorizeRequests()
            .anyRequest().permitAll(); // Permite todas las solicitudes sin autenticación
        return http.build();
    }
}




