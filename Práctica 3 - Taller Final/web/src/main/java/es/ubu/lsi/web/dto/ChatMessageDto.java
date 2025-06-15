
package es.ubu.lsi.web.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferir mensajes de chat entre capas.
 * 
 * @author Ibai Moya Aroz
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {

    /**
     * ID único del mensaje.
     */
    private Long id;

    /**
     * ID del usuario que envía el mensaje.
     */
    private Long senderId;

    /**
     * Nombre del usuario que envía el mensaje.
     */
    private String senderName;

    /**
     * ID del usuario que recibe el mensaje.
     */
    private Long recipientId;

    /**
     * Nombre del usuario que recibe el mensaje.
     */
    private String recipientName;

    /**
     * Contenido del mensaje.
     */
    private String content;

    /**
     * Marca de tiempo del mensaje.
     */
    private LocalDateTime timestamp;

    /**
     * Indica si el mensaje ha sido leído.
     */
    private boolean isRead;

    /**
     * Tipo de mensaje (texto, imagen, etc.).
     */
    private String messageType;
}