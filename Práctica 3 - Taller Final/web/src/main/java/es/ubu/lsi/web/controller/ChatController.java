package es.ubu.lsi.web.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.ubu.lsi.web.dto.ChatMessageDto;
import es.ubu.lsi.web.dto.ConversationDto;
import es.ubu.lsi.web.entity.Usuario;
import es.ubu.lsi.web.repository.UsuarioRepository;
import es.ubu.lsi.web.service.ChatService;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para gestionar operaciones de chat.
 * Proporciona endpoints para obtener conversaciones, historial y estadísticas.
 * 
 * @author Ibai Moya Aroz
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    
    private final ChatService chatService;
    private final UsuarioRepository usuarioRepository;
    
    /**
     * Obtiene todas las conversaciones activas del usuario.
     * 
     * @return lista de conversaciones con último mensaje y conteo de no leídos
     */
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDto>> getConversations() {
        Usuario user = getCurrentUser();
        List<ConversationDto> conversations = chatService.getActiveConversations(user.getId());
        return ResponseEntity.ok(conversations);
    }
    
    /**
     * Obtiene el historial de mensajes con un amigo específico.
     * 
     * @param friendId ID del amigo
     * @param page número de página (default 0)
     * @param size tamaño de página (default 50)
     * @return página de mensajes ordenados por fecha descendente
     */
    @GetMapping("/messages/{friendId}")
    public ResponseEntity<Page<ChatMessageDto>> getMessages(
            @PathVariable Long friendId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        Usuario user = getCurrentUser();
        Page<ChatMessageDto> messages = chatService.getConversation(
            user.getId(), friendId, page, size
        );
        return ResponseEntity.ok(messages);
    }
    
    /**
     * Marca mensajes como leídos.
     * 
     * @param friendId ID del amigo cuyos mensajes se marcarán como leídos
     * @return respuesta exitosa
     */
    @PostMapping("/read/{friendId}")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long friendId) {
        Usuario user = getCurrentUser();
        chatService.markAsRead(user.getId(), friendId);
        return ResponseEntity.ok(Map.of("success", true));
    }
    
    /**
     * Obtiene el conteo total de mensajes no leídos.
     * 
     * @return número de mensajes no leídos
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        Usuario user = getCurrentUser();
        long count = chatService.getUnreadCount(user.getId());
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    /**
     * Obtiene el usuario actualmente autenticado.
     * 
     * @return el usuario actual
     */
    private Usuario getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return usuarioRepository.findByNombre(auth.getName())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}