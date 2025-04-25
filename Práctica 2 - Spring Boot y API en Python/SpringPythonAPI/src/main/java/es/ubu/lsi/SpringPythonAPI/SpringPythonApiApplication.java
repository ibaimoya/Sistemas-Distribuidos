package es.ubu.lsi.SpringPythonAPI;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import es.ubu.lsi.SpringPythonAPI.service.AuthService;

/**
 * Aplicación principal de Spring Boot.
 * 
 * @author Ibai Moya Aroz
 *
 * @version 1.0
 * @since 1.0
 */
@SpringBootApplication
public class SpringPythonApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringPythonApiApplication.class, args);
    }

	/**
	 * Inicializa la aplicación creando un usuario administrador por defecto.
	 * 
	 * @param auth Servicio de autenticación
	 * @return CommandLineRunner que se ejecuta al iniciar la aplicación
	 */
	@SuppressWarnings("unused")  /* Indica que se usa por reflexión de Spring. */
	@Bean
    CommandLineRunner init(AuthService auth) {
        return args -> {
            if (!auth.existeUsuario("admin", "admin@admin.com")) {
                auth.registrar("admin", "admin@admin.com", "admin");
            }
        };
    }
}