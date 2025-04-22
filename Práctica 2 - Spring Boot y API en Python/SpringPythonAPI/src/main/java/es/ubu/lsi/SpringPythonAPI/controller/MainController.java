package es.ubu.lsi.SpringPythonAPI.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String paginaPrincipal() {
        return "index";  // Thymeleaf: src/main/resources/templates/index.html
    }
    
    @GetMapping("/login")
    public String pantallaLogin() {
        return "login";
    }
}