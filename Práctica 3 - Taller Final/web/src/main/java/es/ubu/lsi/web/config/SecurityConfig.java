package es.ubu.lsi.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura la seguridad de la aplicación web.
     * 
     * @param http Seguridad HTTP
     * @return SecurityFilterChain (cadena de filtros de seguridad)
     * @throws Exception Excepción de seguridad
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        /* Permite el acceso a estas rutas sin autenticación. */
        http
                /* Define las rutas públicas y privadas. */
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/login", "/register",
                                "/css/**", "/js/**", "/images/**",
                                "/api/**", "/favicon.ico", "/error/**")
                        .permitAll()
                        .anyRequest().authenticated())

                /* Configuración del formulario de login. */
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/auth")
                        .defaultSuccessUrl("/menu", true)
                        .permitAll())

                /* Configuración del logout. */
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());

        return http.build();
    }
}