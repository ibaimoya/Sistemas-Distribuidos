package es.ubu.lsi.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador principal que maneja las rutas del frontend de la aplicación.
 * Redirige todas las peticiones a la vista de index.html para que React Router
 * pueda manejar el enrutamiento del lado del cliente.
 * 
 * @author Ibai Moya Aroz
 * @version 1.0
 * @since 1.0
 */
@Controller
public class MainController {

    /**
     * Maneja todas las rutas del frontend React.
     * Devuelve index.html para que React Router maneje el routing del lado del
     * cliente.
     * 
     * @return la vista de index.html
     */
    @GetMapping(value = { "/", "/welcome", "/login", "/register", "/my-movies", "/movie/*", "/admin" })
    public String index() {
        return "forward:/index.html";
    }
}