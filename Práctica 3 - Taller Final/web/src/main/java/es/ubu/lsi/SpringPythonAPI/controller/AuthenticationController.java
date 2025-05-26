package es.ubu.lsi.SpringPythonAPI.controller;

import java.util.Collections;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.ubu.lsi.SpringPythonAPI.form.RegistroForm;
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
     * Procesa el formulario de registro de usuario.
     * Valida los datos del formulario y registra al usuario si no hay errores.
     * 
     * @param form Datos del formulario de registro
     * @param errores Errores de validación
     * @param attrs Atributos para redirección
     * @return Vista de registro o redirección a la página de inicio de sesión
     */
    @PostMapping("/register")
    public String procesarRegistro(
            @Valid @ModelAttribute("registroForm") RegistroForm form,
            BindingResult errores,
            RedirectAttributes attrs) {

        // Verifica errores de anotaciones (@NotBlank, @Email)
        if (errores.hasErrors()) {
            // Permite que Thymeleaf muestre errores en el formulario
            return "register";
        }

        // Valida que password y confirm coincidan
        if (!form.getPassword().equals(form.getConfirm())) {
            errores.rejectValue("confirm", "Match", "Las contraseñas no coinciden.");
            return "register";
        }

        // Comprueba existencia de usuario o email
        if (authService.existeUsuario(form.getUsuario(), form.getEmail())) {
            attrs.addFlashAttribute("registerError", "El usuario o el correo ya existe.");
            return "redirect:/register";
        }

        // Registra y redirige a login con mensaje de éxito
        authService.registrar(
                form.getUsuario(),
                form.getEmail(),
                form.getPassword());

        attrs.addFlashAttribute("registerSuccess",
                "Cuenta creada correctamente. Ahora puede iniciar sesión.");
        return "redirect:/login";
    }

    /**
     * Procesa el formulario de inicio de sesión.
     * Valida credenciales y redirige según éxito o error.
     *
     * @param usuario  Nombre de usuario
     * @param password Contraseña en texto plano
     * @param attrs    Atributos de redirección
     * @return Redirección a “/menu” si ok, o a “/login” con mensaje de error.
     */
    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam("usuario") String usuario,
            @RequestParam("password") String password,
            RedirectAttributes attrs) {

        if (authService.login(usuario, password)) {

            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                    usuario, 
                    null, 
                    Collections.emptyList()
                );

            SecurityContextHolder.getContext().setAuthentication(authToken);

            /* Guarda el contexto en la sesión para que persista. */
            HttpSession session = ((ServletRequestAttributes) RequestContextHolder
                                    .currentRequestAttributes())
                                    .getRequest()
                                    .getSession(true);
            session.setAttribute(
                "SPRING_SECURITY_CONTEXT",
                SecurityContextHolder.getContext()
            );

            return "redirect:/menu";
        }

        attrs.addFlashAttribute("loginError", "Credenciales incorrectas.");
        return "redirect:/login";
    }
}