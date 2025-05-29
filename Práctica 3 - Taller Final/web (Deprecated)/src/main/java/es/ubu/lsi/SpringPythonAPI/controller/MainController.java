package es.ubu.lsi.SpringPythonAPI.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.ubu.lsi.SpringPythonAPI.form.RegistroForm;


@Controller
public class MainController {


    @GetMapping("/")
    public String paginaPrincipal(Principal principal) {

        /* Impide volver a zonas para logearse / registrarse. */
        if (principal != null) {
            return "redirect:/menu";
        }

        return "index";
    }

    @GetMapping("/register")
    public String pantallaRegistro(Model model, Principal principal) {

        /* Impide volver a la zona para registrarse. */
        if (principal != null) {
            return "redirect:/menu";
        }        

        if (!model.containsAttribute("registroForm")) {
            model.addAttribute("registroForm", new RegistroForm());
        }
        return "register";
    }

    @GetMapping("/login")
    public String pantallaLogin(Principal principal) {

        /* Impide volver a la zona para logearse. */
        if (principal != null) {
            return "redirect:/menu";
        }

        return "login";
    }

    @GetMapping("/menu")
    public String pantallaMenu(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("usuarioActual", principal.getName());
        }
        return "menu";
    }

    @GetMapping("/pokeAPI")
    public String pantallaPokeAPI(Model model, Principal principal){
        model.addAttribute("usuarioActual", principal.getName());
        return "pokeAPI";
    }
}