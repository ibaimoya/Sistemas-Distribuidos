package es.ubu.lsi.SpringPythonAPI.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.ubu.lsi.SpringPythonAPI.service.AuthService;

/**
 * Controlador de autenticación y registro de usuarios.
 * Proporciona métodos para procesar el inicio de sesión y el registro de nuevos usuarios.
 * 
 * @author Ibai Moya Aroz
 * 
 * @version 1.0
 * @since 1.0
 */
@Controller
public class AuthenticationController {

    /** Servicio de autenticación y registro de usuarios. */
    private final AuthService authService;

    /**
     * Constructor del controlador de autenticación.
     * 
     * @param authService Servicio de autenticación y registro de usuarios
     */
    public AuthenticationController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Procesa el formulario de inicio de sesión.
     * 
     * @param usuario Nombre de usuario
     * @param password Contraseña
     * @param attrs Atributos de redirección para mostrar mensajes de error
     * @return Redirección a la página de inicio o de error
     */
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String usuario,
                                @RequestParam String password,
                                RedirectAttributes attrs) {

        if (authService.login(usuario, password)) {
            return "redirect:/";
        }
        attrs.addFlashAttribute("loginError", "Credenciales incorrectas.");
        return "redirect:/login";
    }

    /**
     * Procesa el formulario de registro de nuevos usuarios.
     * 
     * @param usuario Nombre de usuario
     * @param email Email del usuario
     * @param password Contraseña
     * @param attrs Atributos de redirección para mostrar mensajes de error o éxito
     * @return Redirección a la página de inicio o de error
     */
    @PostMapping("/register")
    public String procesarRegistro(@RequestParam String usuario,
                                   @RequestParam String email,
                                   @RequestParam String password,
                                   RedirectAttributes attrs) {

        if (authService.existeUsuario(usuario, email)) {
            attrs.addFlashAttribute("registerError", "El usuario o el correo ya existe.");
            return "redirect:/register";
        }
        authService.registrar(usuario, email, password);
        attrs.addFlashAttribute("registerSuccess",
                "Cuenta creada correctamente. Ahora puede iniciar sesión.");
        return "redirect:/login";
    }
}