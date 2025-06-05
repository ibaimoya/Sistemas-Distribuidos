package es.ubu.lsi.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

	/**
	 * Método principal que arranca la aplicación web.
	 * 
	 * @param args los argumentos de la línea de comandos
	 */
	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}
}