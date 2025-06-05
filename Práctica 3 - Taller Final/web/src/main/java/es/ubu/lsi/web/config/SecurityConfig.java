package es.ubu.lsi.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

import es.ubu.lsi.web.security.CustomUserDetailsService;

/**
 * Configuración de seguridad para la aplicación web.
 * Define las reglas de autorización y el servicio de detalles del usuario.
 * 
 * @author Ibai Moya Aroz
 * @version 1.0
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Servicio de detalles del usuario personalizado.
     * Se utiliza para cargar los detalles del usuario durante la autenticación.
     */
    private final UserDetailsService uds;
 
    /**
     * Constructor que inicializa el servicio de detalles del usuario.
     * 
     * @param uds el servicio de detalles del usuario personalizado
     */
    public SecurityConfig(CustomUserDetailsService uds) {
        this.uds = uds;
    }

    /**
     * Configura la cadena de filtros de seguridad.
     * Define las reglas de autorización y el servicio de detalles del usuario.
     * 
     * @param http la configuración de seguridad HTTP
     * @return la cadena de filtros de seguridad configurada
     * @throws Exception si ocurre un error al configurar la seguridad
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
        .requestMatchers("/admin/**").hasRole("ADMIN")
        .requestMatchers("/api/**").authenticated()
        .anyRequest().permitAll()
        )
        .exceptionHandling(ex -> ex
        .authenticationEntryPoint((req, res, e) -> res.sendRedirect("/login"))
        .accessDeniedHandler((req, res, e) -> res.sendRedirect("/"))
        )
        .userDetailsService(uds)
        .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}