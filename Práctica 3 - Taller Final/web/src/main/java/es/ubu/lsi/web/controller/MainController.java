package es.ubu.lsi.web.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.ubu.lsi.web.form.RegisterForm;

@Controller
public class MainController {
    String REDIRECCION_HOME = "redirect:/home";

    @GetMapping("/")
    public String paginaPrincipal(Principal principal) {

        /* Impide volver a zonas para logearse / registrarse. */
        if (principal != null) {
            return REDIRECCION_HOME;
        }

        return "index";
    }

    @GetMapping("/register")
    public String pantallaRegistro(Model model, Principal principal) {

        /* Impide volver a la zona para registrarse. */
        if (principal != null) {
            return REDIRECCION_HOME;
        }

        if (!model.containsAttribute("registroForm")) {
            model.addAttribute("registroForm", new RegisterForm());
        }
        return "register";
    }

    @GetMapping("/login")
    public String pantallaLogin(Principal principal) {

        /* Impide volver a la zona para logearse. */
        if (principal != null) {
            return REDIRECCION_HOME;
        }

        return "login";
    }

    @GetMapping("/home")
    public String pantallaHome(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("usuarioActual", principal.getName());
        }
        return "home";
    }
}