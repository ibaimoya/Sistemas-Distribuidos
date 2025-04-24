package es.ubu.lsi.SpringPythonAPI.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.ubu.lsi.SpringPythonAPI.form.RegistroForm;


@Controller
public class MainController {

    @GetMapping("/")
    public String paginaPrincipal() {
        return "index";
    }

    @GetMapping("/register")
    public String pantallaRegistro(Model model) {

        if (!model.containsAttribute("registroForm")) {
            model.addAttribute("registroForm", new RegistroForm());
        }
        return "register";
    }

    @GetMapping("/login")
    public String pantallaLogin() {
        return "login";
    }
}