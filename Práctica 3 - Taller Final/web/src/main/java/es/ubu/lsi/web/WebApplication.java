package es.ubu.lsi.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import es.ubu.lsi.web.service.AuthService;

/**
 * Clase principal de la aplicación web.
 * Inicializa la aplicación y crea un usuario administrador por defecto.
 * 
 * @author Ibai Moya Aroz
 *
 * @version 1.0
 * @since 1.0
 */
@SpringBootApplication
public class WebApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}

	/**
	 * Inicializa la aplicación creando un usuario administrador por defecto.
	 * 
	 * @param auth Servicio de autenticación
	 * @return CommandLineRunner que se ejecuta al iniciar la aplicación
	 */
	@Bean
	CommandLineRunner init(AuthService auth) {
		String nombre = "admin";
		String email = "admin@admin.com";
		String password = nombre;

		return args -> {
			if (!auth.existeUsuario(nombre, email)) {
				auth.registrar(nombre, email, password);
			}
		};
	}
}