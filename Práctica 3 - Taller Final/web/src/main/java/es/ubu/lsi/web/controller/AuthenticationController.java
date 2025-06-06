package es.ubu.lsi.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import es.ubu.lsi.web.dto.LoginRequest;
import es.ubu.lsi.web.dto.RegisterRequest;
import es.ubu.lsi.web.entity.Role;
import es.ubu.lsi.web.entity.Usuario;
import es.ubu.lsi.web.repository.UsuarioRepository;
import es.ubu.lsi.web.service.AuthService;
import jakarta.servlet.http.HttpServletRequest; 
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la autenticación de usuarios.
 * Permite registrar nuevos usuarios, iniciar sesión, cerrar sesión y verificar el estado de autenticación.
 * 
 * @author Ibai Moya Aroz
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    /* Clave para indicar el éxito de la operación. */
    private static final String SUCCESS_KEY = "success";

    /* Clave para indicar el mensaje de respuesta. */
    private static final String MESSAGE_KEY = "message";

    /* Clave para almacenar el texto literal de nombre de usuario. */
    private static final String USERNAME_KEY = "username";
    
    /* Clave para indicar si el usuario está autenticado. */
    private static final String AUTHENTICATED_KEY = "authenticated";

    /* Clave para indicar si el usuario es administrador. */
    private static final String ADMIN_KEY = "admin";

    /** Servicio de autenticación para manejar el registro y login de usuarios. */
    private final AuthService        authService;

    /** Repositorio de usuarios para acceder a la base de datos. */
    private final UsuarioRepository  usuarioRepo;

    /**
     * Procesa el registro de un nuevo usuario.
     * Valida que las contraseñas coincidan y que el usuario/email no exista.
     * Si todo es correcto, registra al usuario y devuelve una respuesta exitosa.
     * 
     * @param request los datos del registro
     * @return una respuesta con el resultado del registro
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> procesarRegistro(@Valid @RequestBody RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();

        /* Valida que password y confirm coincidan. */
        if (!request.getPassword().equals(request.getConfirm())) {
            response.put(SUCCESS_KEY, false);
            response.put(MESSAGE_KEY, "Las contraseñas no coinciden");
            return ResponseEntity.badRequest().body(response);
        }

        /* Comprueba existencia de usuario o email. */
        if (authService.existeUsuario(request.getUsuario(), request.getEmail())) {
            response.put(SUCCESS_KEY, false);
            response.put(MESSAGE_KEY, "El usuario o el correo ya existe");
            return ResponseEntity.badRequest().body(response);
        }

        /* Registra al usuario. */
        authService.registrar(
                request.getUsuario(),
                request.getEmail(),
                request.getPassword());

        response.put(SUCCESS_KEY, true);
        response.put(MESSAGE_KEY, "Cuenta creada correctamente");
        return ResponseEntity.ok(response);
    }

    /**
     * Procesa el inicio de sesión de un usuario.
     * Valida las credenciales y, si son correctas, establece el contexto de seguridad.
     * 
     * @param request los datos del inicio de sesión
     * @return una respuesta con el resultado del inicio de sesión
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> procesarLogin(@Valid @RequestBody LoginRequest request) {

    Map<String, Object> res = new HashMap<>();

    /* Comprueba usuario y contraseña. */
    if (!authService.login(request.getUsername(), request.getPassword())) {
        res.put(SUCCESS_KEY, false);
        res.put(MESSAGE_KEY, "Credenciales incorrectas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
    }

    Usuario u = usuarioRepo.findByNombre(request.getUsername()).orElseThrow();

    List<GrantedAuthority> auths = List.of(
            new SimpleGrantedAuthority("ROLE_" + u.getRole().name())
    );

    UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                    u.getNombre(),
                    null,
                    auths 
            );

    SecurityContextHolder.getContext().setAuthentication(authToken);

    HttpSession session = ((ServletRequestAttributes) RequestContextHolder
            .currentRequestAttributes())
            .getRequest()
            .getSession(true);

    session.setAttribute("SPRING_SECURITY_CONTEXT",
                         SecurityContextHolder.getContext());

    res.put(SUCCESS_KEY,  true);
    res.put(USERNAME_KEY, u.getNombre());
    return ResponseEntity.ok(res);
}

    /**
     * Cierra la sesión del usuario actual.
     * Limpia el contexto de seguridad y invalida la sesión HTTP.
     * 
     * @param request la solicitud HTTP
     * @return una respuesta con el resultado del cierre de sesión
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Map<String, Object> response = new HashMap<>();
        response.put(SUCCESS_KEY, true);
        response.put(MESSAGE_KEY, "Sesión cerrada");
        return ResponseEntity.ok(response);
    }

    /**
     * Verifica si el usuario está autenticado.
     * Devuelve un mapa con el estado de autenticación y, si está autenticado,
     * el nombre de usuario y si es administrador.
     * 
     * @param request la solicitud HTTP
     * @return una respuesta con el estado de autenticación
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkAuth(HttpServletRequest request) {

        Map<String, Object> res = new HashMap<>();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean logged = auth != null && auth.isAuthenticated()
                        && !"anonymousUser".equals(auth.getPrincipal());

        if (!logged) {
            res.put(AUTHENTICATED_KEY, false);
            return ResponseEntity.ok(res);
        }

        /* Busca al usuario en la base de datos para saber su rol. */
        Usuario u = null;
        String username = (auth != null && auth.getName() != null) ? auth.getName() : null;
        if (username != null) {
            u = usuarioRepo.findByNombre(username).orElse(null);
        }

        if (u == null) {
            res.put(AUTHENTICATED_KEY, false);
            return ResponseEntity.ok(res);
        }

        res.put(AUTHENTICATED_KEY, true);
        res.put(USERNAME_KEY,      u.getNombre());
        res.put(ADMIN_KEY,         u.getRole() == Role.ADMIN);
        return ResponseEntity.ok(res);
    }
}