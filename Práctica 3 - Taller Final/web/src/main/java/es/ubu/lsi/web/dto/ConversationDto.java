package es.ubu.lsi.web.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar una conversación en la lista de chats.
 * 
 * @author Ibai Moya Aroz
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationDto {

    /**
     * ID único de la conversación.
     */
    private Long friendId;
    
    /**
     * Nombre del amigo en la conversación.
     */
    private String friendName;

    /**
     * Último mensaje en la conversación.
     */
    private String lastMessage;

    /**
     * Marca de tiempo del último mensaje.
     */
    private LocalDateTime lastMessageTime;

    /**
     * Contador de mensajes no leídos.
     */
    private long unreadCount;

    /**
     * Indica si el último mensaje fue enviado por el usuario actual.
     */
    private boolean isLastMessageFromMe;
}