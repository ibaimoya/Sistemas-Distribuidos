package es.ubu.lsi.web.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un mensaje de chat entre dos usuarios.
 * Almacena el contenido del mensaje, remitente, destinatario y metadatos.
 * 
 * @author Ibai Moya Aroz
 * 
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    
    /**
     * ID único del mensaje.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Usuario que envía el mensaje.
     */
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Usuario sender;
    
    /**
     * Usuario que recibe el mensaje.
     */
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private Usuario recipient;
    
    /**
     * Contenido del mensaje.
     */
    @Column(nullable = false, length = 1000)
    private String content;
    
    /**
     * Fecha y hora cuando se envió el mensaje.
     */
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    /**
     * Indica si el mensaje ha sido leído por el destinatario.
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean isRead = false;
    
    /**
     * Tipo de mensaje (TEXT, IMAGE, etc). Por ahora solo TEXT.
     */
    @Column(nullable = false)
    @Builder.Default
    private String messageType = "TEXT";
}