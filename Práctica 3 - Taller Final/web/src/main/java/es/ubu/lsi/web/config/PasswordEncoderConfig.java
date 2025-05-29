package es.ubu.lsi.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración de codificador de contraseñas.
 * 
 * @author Ibai Moya Aroz
 *
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Crea un objeto de codificador de contraseñas.
     * Utiliza BCrypt para hashear las contraseñas.
     * 
     * @return PasswordEncoder Bean (objeto) de codificador de contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); /* Devuelve el codificador. */
    }
}