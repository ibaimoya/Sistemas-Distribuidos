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

    /**
     * Configuración de seguridad para la aplicación web.
     * Esta configuración protege las APIs y desactiva el formulario de login automático.
     * Permite el acceso a todas las demás rutas sin autenticación, ya que esto se gestiona
     * desde el propio FrontEnd (en ProtectedRoute.tsx).
     * @param http HttpSecurity para configurar la seguridad web
     * @return SecurityFilterChain configurado
     * @throws Exception si ocurre un error durante la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()  // Todo lo demás es libre
            )
            
            /* Desactiva el login form automático.  */
            .formLogin(form -> form.disable())

            /* Configuración de logout. (Comprobar si realmente es útil) */
            .logout(logout -> logout
                .logoutUrl("/api/logout")
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                .permitAll())

            /* Para APIs no autenticadas, devuelve 401. */
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            
            // Desactiva CSRF (Cambiar en un futuro).
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}