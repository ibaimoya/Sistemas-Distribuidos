package es.ubu.lsi.web.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import es.ubu.lsi.web.entity.ChatMessage;
import es.ubu.lsi.web.entity.Usuario;

/**
 * Repositorio para gestionar las operaciones de mensajes de chat en la base de datos.
 * Proporciona métodos para buscar conversaciones, contar mensajes no leídos y marcar como leídos.
 * 
 * @author Ibai Moya Aroz
 * 
 * @version 1.0
 * @since 1.0
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    /**
     * Busca mensajes entre dos usuarios (conversación bidireccional).
     * 
     * @param user1 primer usuario
     * @param user2 segundo usuario  
     * @param pageable información de paginación
     * @return página de mensajes ordenados por timestamp descendente
     */
    @Query("SELECT m FROM ChatMessage m WHERE " +
           "(m.sender = :user1 AND m.recipient = :user2) OR " +
           "(m.sender = :user2 AND m.recipient = :user1) " +
           "ORDER BY m.timestamp DESC")
    Page<ChatMessage> findConversation(@Param("user1") Usuario user1, 
                                       @Param("user2") Usuario user2, 
                                       Pageable pageable);
    
    /**
     * Cuenta los mensajes no leídos de un remitente específico a un destinatario.
     * 
     * @param sender remitente de los mensajes
     * @param recipient destinatario de los mensajes
     * @return número de mensajes no leídos
     */
    long countBySenderAndRecipientAndIsReadFalse(Usuario sender, Usuario recipient);
    
    /**
     * Marca todos los mensajes como leídos de un remitente a un destinatario.
     * 
     * @param sender remitente de los mensajes
     * @param recipient destinatario de los mensajes
     */
    @Modifying
    @Transactional
    @Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.sender = :sender AND m.recipient = :recipient AND m.isRead = false")
    void markMessagesAsRead(@Param("sender") Usuario sender, @Param("recipient") Usuario recipient);
    
    /**
     * Obtiene los últimos mensajes con cada amigo (para preview en lista de chats).
     * 
     * @param user usuario actual
     * @return lista de los últimos mensajes con cada contacto
     */
    @Query("SELECT m FROM ChatMessage m WHERE m.id IN (" +
           "SELECT MAX(m2.id) FROM ChatMessage m2 " +
           "WHERE m2.sender = :user OR m2.recipient = :user " +
           "GROUP BY CASE " +
           "    WHEN m2.sender = :user THEN m2.recipient.id " +
           "    ELSE m2.sender.id " +
           "END" +
           ")")
    List<ChatMessage> findLastMessagesWithEachContact(@Param("user") Usuario user);
    
    /**
     * Cuenta el total de mensajes no leídos para un usuario.
     * 
     * @param recipient usuario destinatario
     * @return número total de mensajes no leídos
     */
    long countByRecipientAndIsReadFalse(Usuario recipient);
}