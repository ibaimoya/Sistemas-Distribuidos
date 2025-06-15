package es.ubu.lsi.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para recibir solicitudes de envío de mensajes via WebSocket.
 * 
 * @author Ibai Moya Aroz
 * 
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {

    /**
     * ID del usuario que envía el mensaje.
     */
    private Long recipientId;

    /**
     * Contenido del mensaje.
     */
    private String content;
}