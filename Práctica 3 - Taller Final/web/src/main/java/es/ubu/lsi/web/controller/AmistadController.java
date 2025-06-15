package es.ubu.lsi.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.ubu.lsi.web.entity.Amistad;
import es.ubu.lsi.web.entity.SolicitudAmistad;
import es.ubu.lsi.web.entity.Usuario;
import es.ubu.lsi.web.repository.UsuarioRepository;
import es.ubu.lsi.web.service.AmistadService;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para gestionar las amistades entre usuarios.
 * Mantiene todos los endpoints que estaban duplicados en FriendController.
 *
 * @author Ibai Moya Aroz
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class AmistadController {

    private final AmistadService amistadService;
    private final UsuarioRepository usuarioRepository;

    /** Obtiene el usuario autenticado actual. */
    private Usuario obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        return usuarioRepository.findByNombre(auth.getName()).orElse(null);
    }

    /** Devuelve la lista de amigos del usuario actual. */
    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerAmigos() {
        Usuario usuario = obtenerUsuarioActual();
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Amistad> amistades = amistadService.obtenerAmigos(usuario);
        List<Map<String, Object>> amigos = amistades.stream()
            .map(a -> Map.<String, Object>of(
                    "id", a.getAmigo().getId(),
                    "nombre", a.getAmigo().getNombre(),
                    "email", a.getAmigo().getEmail(),
                    "fechaAmistad", a.getFechaAmistad().toString()
            ))
            .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("friends", amigos);
        response.put("total", amigos.size());
        return ResponseEntity.ok(response);
    }

    /** Envía una solicitud de amistad. */
    @PostMapping("/request")
    public ResponseEntity<Map<String, Object>> enviarSolicitud(@RequestBody Map<String, String> body) {
        Usuario remitente = obtenerUsuarioActual();
        if (remitente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> response = new HashMap<>();
        String nombreDestinatario = body.get("username");
        if (nombreDestinatario == null || nombreDestinatario.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Nombre de usuario requerido");
            return ResponseEntity.badRequest().body(response);
        }

        Usuario destinatario = usuarioRepository.findByNombre(nombreDestinatario).orElse(null);
        if (destinatario == null) {
            response.put("success", false);
            response.put("message", "Usuario no encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            SolicitudAmistad solicitud = amistadService.enviarSolicitud(remitente, destinatario);
            response.put("success", true);
            response.put("message", "Solicitud de amistad enviada");
            response.put("requestId", solicitud.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /** Devuelve las solicitudes pendientes recibidas. */
    @GetMapping("/requests/pending")
    public ResponseEntity<Map<String, Object>> obtenerSolicitudesPendientes() {
        Usuario usuario = obtenerUsuarioActual();
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<SolicitudAmistad> solicitudes = amistadService.obtenerSolicitudesPendientes(usuario);
        List<Map<String, Object>> datos = solicitudes.stream()
            .map(s -> Map.<String, Object>of(
                    "id", s.getId(),
                    "remitente", Map.of(
                            "id", s.getRemitente().getId(),
                            "nombre", s.getRemitente().getNombre(),
                            "email", s.getRemitente().getEmail()
                    ),
                    "fecha", s.getFechaSolicitud().toString()
            ))
            .toList();

        return ResponseEntity.ok(Map.of(
                "requests", datos,
                "total", datos.size()
        ));
    }

    /** Devuelve las solicitudes enviadas por el usuario. */
    @GetMapping("/requests/sent")
    public ResponseEntity<Map<String, Object>> obtenerSolicitudesEnviadas() {
        Usuario usuario = obtenerUsuarioActual();
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<SolicitudAmistad> solicitudes = amistadService.obtenerSolicitudesEnviadas(usuario);
        List<Map<String, Object>> datos = solicitudes.stream()
            .map(s -> Map.<String, Object>of(
                    "id", s.getId(),
                    "destinatario", Map.of(
                            "id", s.getDestinatario().getId(),
                            "nombre", s.getDestinatario().getNombre(),
                            "email", s.getDestinatario().getEmail()
                    ),
                    "estado", s.getEstado().toString(),
                    "fecha", s.getFechaSolicitud().toString()
            ))
            .toList();

        return ResponseEntity.ok(Map.of(
                "requests", datos,
                "total", datos.size()
        ));
    }

    /** Acepta una solicitud de amistad. */
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<Map<String, Object>> aceptarSolicitud(@PathVariable Long requestId) {
        Usuario usuario = obtenerUsuarioActual();
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> response = new HashMap<>();
        try {
            amistadService.aceptarSolicitud(requestId, usuario);
            response.put("success", true);
            response.put("message", "Solicitud aceptada");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /** Rechaza una solicitud de amistad. */
    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<Map<String, Object>> rechazarSolicitud(@PathVariable Long requestId) {
        Usuario usuario = obtenerUsuarioActual();
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> response = new HashMap<>();
        try {
            amistadService.rechazarSolicitud(requestId, usuario);
            response.put("success", true);
            response.put("message", "Solicitud rechazada");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /** Cancela una solicitud enviada. */
    @DeleteMapping("/requests/{requestId}")
    public ResponseEntity<Map<String, Object>> cancelarSolicitud(@PathVariable Long requestId) {
        Usuario usuario = obtenerUsuarioActual();
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> response = new HashMap<>();
        try {
            amistadService.cancelarSolicitud(requestId, usuario);
            response.put("success", true);
            response.put("message", "Solicitud cancelada");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /** Elimina un amigo. */
    @DeleteMapping("/{friendId}")
    public ResponseEntity<Map<String, Object>> eliminarAmigo(@PathVariable Long friendId) {
        Usuario usuario = obtenerUsuarioActual();
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> response = new HashMap<>();
        try {
            amistadService.eliminarAmigo(usuario, friendId);
            response.put("success", true);
            response.put("message", "Amigo eliminado");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /** Devuelve el número de solicitudes pendientes para notificaciones. */
    @GetMapping("/requests/count")
    public ResponseEntity<Map<String, Object>> contarSolicitudesPendientes() {
        Usuario usuario = obtenerUsuarioActual();
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        long count = amistadService.contarSolicitudesPendientes(usuario);
        return ResponseEntity.ok(Map.of("count", count));
    }
}
