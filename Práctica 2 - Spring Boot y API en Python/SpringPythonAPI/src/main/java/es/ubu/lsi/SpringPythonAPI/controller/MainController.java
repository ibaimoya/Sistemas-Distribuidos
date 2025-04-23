package es.ubu.lsi.SpringPythonAPI.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String paginaPrincipal() {
        return "index";
    }

    @GetMapping("/register")
    public String pantallaRegistro() {
        return "register";
    }

    @GetMapping("/login")
    public String pantallaLogin() {
        return "login";
    }
}