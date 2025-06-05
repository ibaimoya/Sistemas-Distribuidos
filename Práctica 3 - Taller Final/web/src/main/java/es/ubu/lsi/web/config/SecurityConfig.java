package es.ubu.lsi.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                // Configuración de autorización
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/",
                                                                "/index.html",
                                                                "/static/**",
                                                                "/assets/**",
                                                                "/*.js",
                                                                "/*.css",
                                                                "/*.ico",
                                                                "/login",
                                                                "/register",
                                                                "/auth/**",
                                                                "/api/public/**",
                                                                "robots.txt")
                                                .permitAll()
                                                .requestMatchers("/api/**").authenticated()
                                                .anyRequest().permitAll() // Permite todo lo demás para React Router
                                )

                                // Desactiva el login form automático de Spring
                                .formLogin(form -> form.disable())

                                // Configuración de logout
                                .logout(logout -> logout
                                                .logoutUrl("/api/logout")
                                                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(
                                                                HttpStatus.OK))
                                                .permitAll())

                                // Para peticiones no autenticadas, devuelve 401 en lugar de redirigir
                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(
                                                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

                                // Desactiva CSRF para simplificar (puedes habilitarlo más tarde)
                                .csrf(csrf -> csrf.disable());

                return http.build();
        }
}