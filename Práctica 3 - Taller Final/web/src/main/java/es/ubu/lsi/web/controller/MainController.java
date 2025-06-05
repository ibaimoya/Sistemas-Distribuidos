package es.ubu.lsi.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    /**
     * Maneja todas las rutas del frontend React.
     * Devuelve index.html para que React Router maneje el routing del lado del
     * cliente.
     * 
     * @return la vista de index.html
     */
    @GetMapping(value = { "/", "/login", "/register", "/movie/*", "/app/**" })
    public String index() {
        return "forward:/index.html";
    }
}