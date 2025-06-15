package es.ubu.lsi.web.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import es.ubu.lsi.web.dto.ChatMessageDto;
import es.ubu.lsi.web.dto.ChatMessageRequest;
import es.ubu.lsi.web.entity.Usuario;
import es.ubu.lsi.web.repository.UsuarioRepository;
import es.ubu.lsi.web.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador WebSocket para manejar mensajes de chat en tiempo real.
 * Utiliza STOMP sobre WebSocket para comunicación bidireccional.
 * 
 * @author Ibai Moya Aroz
 * @version 1.0
 * @since 1.0
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final UsuarioRepository usuarioRepository;
    
    /**
     * Maneja el envío de mensajes de chat.
     * Recibe un mensaje del cliente y lo reenvía al destinatario.
     * 
     * @param messageRequest el mensaje a enviar
     * @param principal información del usuario autenticado
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest messageRequest, Principal principal) {
        try {
            // Obtener el usuario remitente
            Usuario sender = usuarioRepository.findByNombre(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Enviar el mensaje usando el servicio
            ChatMessageDto savedMessage = chatService.sendMessage(
                sender.getId(),
                messageRequest.getRecipientId(),
                messageRequest.getContent()
            );
            
            // Enviar al destinatario por su canal privado
            messagingTemplate.convertAndSendToUser(
                messageRequest.getRecipientId().toString(),
                "/queue/messages",
                savedMessage
            );
            
            // También enviar confirmación al remitente
            messagingTemplate.convertAndSendToUser(
                sender.getId().toString(),
                "/queue/messages",
                savedMessage
            );
            
            log.info("Mensaje enviado de {} a usuario {}", 
                    principal.getName(), messageRequest.getRecipientId());
                    
        } catch (Exception e) {
            log.error("Error enviando mensaje: ", e);
            // Enviar error al remitente
            messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/errors",
                "Error al enviar mensaje: " + e.getMessage()
            );
        }
    }
    
    /**
     * Notifica que un usuario está escribiendo.
     * 
     * @param recipientId ID del destinatario
     * @param principal información del usuario que escribe
     */
    @MessageMapping("/chat.typing")
    public void typing(@Payload Long recipientId, Principal principal) {
        Usuario sender = usuarioRepository.findByNombre(principal.getName())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
        messagingTemplate.convertAndSendToUser(
            recipientId.toString(),
            "/queue/typing",
            sender.getId()
        );
    }
    
    /**
     * Marca mensajes como leídos.
     * 
     * @param senderId ID del remitente de los mensajes a marcar
     * @param principal información del usuario que lee
     */
    @MessageMapping("/chat.markAsRead")
    public void markAsRead(@Payload Long senderId, Principal principal) {
        Usuario reader = usuarioRepository.findByNombre(principal.getName())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
        chatService.markAsRead(reader.getId(), senderId);
        
        // Notificar al remitente que sus mensajes fueron leídos
        messagingTemplate.convertAndSendToUser(
            senderId.toString(),
            "/queue/read",
            reader.getId()
        );
    }
}