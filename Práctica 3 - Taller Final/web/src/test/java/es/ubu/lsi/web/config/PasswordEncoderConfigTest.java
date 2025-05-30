package es.ubu.lsi.web.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Tests para la configuración de codificador de contraseñas.
 * 
 * @author Test
 * @version 1.0
 * @since 1.0
 */
class PasswordEncoderConfigTest {

    /**
     * Verifica la creación manual del bean.
     */
    @Test
    void testPasswordEncoderBeanDirectInstantiation() {
        PasswordEncoderConfig config = new PasswordEncoderConfig();
        PasswordEncoder encoder = config.passwordEncoder();
        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
    }

    /**
     * Verifica la carga del contexto de Spring y la inyección del bean.
     */
    @Test
    void testPasswordEncoderBeanInSpringContext() {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(PasswordEncoderConfig.class)) {
            PasswordEncoder encoder = ctx.getBean(PasswordEncoder.class);
            assertNotNull(encoder);
            assertTrue(encoder instanceof BCryptPasswordEncoder);
        }
    }

    /**
     * Verifica que encode transforma la contraseña y que matches la valida correctamente.
     */
    @Test
    void testEncodeAndMatches() {
        PasswordEncoderConfig config = new PasswordEncoderConfig();
        PasswordEncoder encoder = config.passwordEncoder();
        String raw = "miSecreta123";
        String hash1 = encoder.encode(raw);
        String hash2 = encoder.encode(raw);
        
        // Comprueba que el hash no es igual al texto plano.
        assertNotEquals(raw, hash1);
        // Comprueba que dos codificaciones de la misma cadena difieren por el salt.
        assertNotEquals(hash1, hash2);
        // Comprueba que matches reconoce la contraseña correcta.
        assertTrue(encoder.matches(raw, hash1));
        // Comprueba que matches rechaza una contraseña incorrecta.
        assertFalse(encoder.matches("otra", hash1));
    }

}