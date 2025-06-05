package es.ubu.lsi.web.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import es.ubu.lsi.web.dto.LoginRequest;
import es.ubu.lsi.web.dto.RegisterRequest;
import es.ubu.lsi.web.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthService authService;

    public AuthenticationController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> procesarRegistro(@Valid @RequestBody RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();

        // Valida que password y confirm coincidan
        if (!request.getPassword().equals(request.getConfirm())) {
            response.put("success", false);
            response.put("message", "Las contraseñas no coinciden");
            return ResponseEntity.badRequest().body(response);
        }

        // Comprueba existencia de usuario o email
        if (authService.existeUsuario(request.getUsuario(), request.getEmail())) {
            response.put("success", false);
            response.put("message", "El usuario o el correo ya existe");
            return ResponseEntity.badRequest().body(response);
        }

        // Registra al usuario
        authService.registrar(
                request.getUsuario(),
                request.getEmail(),
                request.getPassword());

        response.put("success", true);
        response.put("message", "Cuenta creada correctamente");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> procesarLogin(@Valid @RequestBody LoginRequest request) {

        Map<String, Object> response = new HashMap<>();

        if (authService.login(request.getUsername(), request.getPassword())) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
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
            response.put("username", request.getUsername());
            return ResponseEntity.ok(response);
        }

        response.put("success", false);
        response.put("message", "Credenciales incorrectas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        SecurityContextHolder.clearContext();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Sesión cerrada");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkAuth() {
        Map<String, Object> response = new HashMap<>();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() &&
                !auth.getPrincipal().equals("anonymousUser")) {
            response.put("authenticated", true);
            response.put("username", auth.getName());
        } else {
            response.put("authenticated", false);
        }

        return ResponseEntity.ok(response);
    }
}