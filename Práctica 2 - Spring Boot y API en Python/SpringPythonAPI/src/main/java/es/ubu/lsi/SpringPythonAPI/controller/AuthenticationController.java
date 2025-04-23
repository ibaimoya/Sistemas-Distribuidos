package es.ubu.lsi.SpringPythonAPI.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthenticationController {

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String usuario,
                                @RequestParam String password,
                                RedirectAttributes redirectAttrs) {
        // Valida credenciales (ejemplo simple, cambiar a futuro) !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        if ("admin".equals(usuario) && "1234".equals(password)) {
            return "redirect:/";
        } else {
            redirectAttrs.addFlashAttribute("loginError", "Credenciales incorrectas.");
            return "redirect:/login";
        }
    }
}