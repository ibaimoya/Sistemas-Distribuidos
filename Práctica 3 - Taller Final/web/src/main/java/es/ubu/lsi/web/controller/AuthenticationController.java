package es.ubu.lsi.web.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import es.ubu.lsi.web.form.RegisterForm;
import es.ubu.lsi.web.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthService authService;

    public AuthenticationController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> procesarRegistro(@Valid @RequestBody RegisterForm form) {
        Map<String, Object> response = new HashMap<>();

        // Valida que password y confirm coincidan
        if (!form.getPassword().equals(form.getConfirm())) {
            response.put("success", false);
            response.put("message", "Las contraseñas no coinciden");
            return ResponseEntity.badRequest().body(response);
        }

        // Comprueba existencia de usuario o email
        if (authService.existeUsuario(form.getUsuario(), form.getEmail())) {
            response.put("success", false);
            response.put("message", "El usuario o el correo ya existe");
            return ResponseEntity.badRequest().body(response);
        }

        // Registra al usuario
        authService.registrar(
                form.getUsuario(),
                form.getEmail(),
                form.getPassword());

        response.put("success", true);
        response.put("message", "Cuenta creada correctamente");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> procesarLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {

        Map<String, Object> response = new HashMap<>();

        if (authService.login(username, password)) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    Collections.emptyList());

            SecurityContextHolder.getContext().setAuthentication(authToken);

            HttpSession session = ((ServletRequestAttributes) RequestContextHolder
                    .currentRequestAttributes())
                    .getRequest()
                    .getSession(true);
            session.setAttribute(
                    "SPRING_SECURITY_CONTEXT",
                    SecurityContextHolder.getContext());

            response.put("success", true);
            response.put("username", username);
            return ResponseEntity.ok(response);
        }

        response.put("success", false);
        response.put("message", "Credenciales incorrectas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Sesión cerrada");
        return ResponseEntity.ok(response);
    }
}