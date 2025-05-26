package es.ubu.lsi.SpringPythonAPI.config;

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
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .antMatchers("/", "/login", "/register",
                             "/css/**", "/js/**", "/images/**", "/api/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/auth")
                .defaultSuccessUrl("/menu", true) 
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            );
        return http.build();
    }
}