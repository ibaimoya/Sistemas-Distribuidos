package es.ubu.lsi.web.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.ubu.lsi.web.dto.ChatMessageDto;
import es.ubu.lsi.web.dto.ConversationDto;
import es.ubu.lsi.web.entity.ChatMessage;
import es.ubu.lsi.web.entity.Usuario;
import es.ubu.lsi.web.repository.AmistadRepository;
import es.ubu.lsi.web.repository.ChatMessageRepository;
import es.ubu.lsi.web.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio que gestiona la lógica de negocio del chat.
 * Maneja el envío, recepción y gestión de mensajes entre usuarios.
 * 
 * @author Ibai Moya Aroz
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatService {
    
    private final ChatMessageRepository messageRepository;
    private final UsuarioRepository usuarioRepository;
    private final AmistadRepository amistadRepository;
    
    /**
     * Envía un mensaje de un usuario a otro.
     * 
     * @param senderId ID del remitente
     * @param recipientId ID del destinatario
     * @param content contenido del mensaje
     * @return el mensaje enviado como DTO
     */
    public ChatMessageDto sendMessage(Long senderId, Long recipientId, String content) {
        Usuario sender = usuarioRepository.findById(senderId)
            .orElseThrow(() -> new RuntimeException("Remitente no encontrado"));
        Usuario recipient = usuarioRepository.findById(recipientId)
            .orElseThrow(() -> new RuntimeException("Destinatario no encontrado"));
        
        // Verificar que son amigos
        boolean areTheyFriends = amistadRepository.existsByUsuarioAndAmigo(sender, recipient) ||
                               amistadRepository.existsByUsuarioAndAmigo(recipient, sender);
        
        if (!areTheyFriends) {
            throw new RuntimeException("Solo puedes enviar mensajes a tus amigos");
        }
        
        ChatMessage message = ChatMessage.builder()
            .sender(sender)
            .recipient(recipient)
            .content(content)
            .timestamp(LocalDateTime.now())
            .isRead(false)
            .messageType("TEXT")
            .build();
        
        message = messageRepository.save(message);
        log.info("Mensaje enviado de {} a {}", sender.getNombre(), recipient.getNombre());
        
        return toDto(message);
    }
    
    /**
     * Obtiene el historial de conversación entre dos usuarios.
     * 
     * @param userId ID del usuario actual
     * @param friendId ID del amigo
     * @param page número de página
     * @param size tamaño de página
     * @return página de mensajes
     */
    public Page<ChatMessageDto> getConversation(Long userId, Long friendId, int page, int size) {
        Usuario user = usuarioRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Usuario friend = usuarioRepository.findById(friendId)
            .orElseThrow(() -> new RuntimeException("Amigo no encontrado"));
        
        Page<ChatMessage> messages = messageRepository.findConversation(
            user, friend, PageRequest.of(page, size)
        );
        
        // Marcar mensajes como leídos
        messageRepository.markMessagesAsRead(friend, user);
        
        return messages.map(this::toDto);
    }
    
    /**
     * Obtiene todas las conversaciones activas del usuario.
     * 
     * @param userId ID del usuario
     * @return lista de conversaciones con el último mensaje
     */
    public List<ConversationDto> getActiveConversations(Long userId) {
        Usuario user = usuarioRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        List<ChatMessage> lastMessages = messageRepository.findLastMessagesWithEachContact(user);
        
        return lastMessages.stream().map(msg -> {
            Usuario otherUser = msg.getSender().getId().equals(userId) 
                ? msg.getRecipient() 
                : msg.getSender();
            
            long unreadCount = messageRepository.countBySenderAndRecipientAndIsReadFalse(otherUser, user);
            
            return ConversationDto.builder()
                .friendId(otherUser.getId())
                .friendName(otherUser.getNombre())
                .lastMessage(msg.getContent())
                .lastMessageTime(msg.getTimestamp())
                .unreadCount(unreadCount)
                .isLastMessageFromMe(msg.getSender().getId().equals(userId))
                .build();
        }).collect(Collectors.toList());
    }
    
    /**
     * Marca mensajes como leídos.
     * 
     * @param userId ID del usuario que lee
     * @param senderId ID del remitente de los mensajes
     */
    public void markAsRead(Long userId, Long senderId) {
        Usuario user = usuarioRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Usuario sender = usuarioRepository.findById(senderId)
            .orElseThrow(() -> new RuntimeException("Remitente no encontrado"));
        
        messageRepository.markMessagesAsRead(sender, user);
    }
    
    /**
     * Obtiene el número total de mensajes no leídos.
     * 
     * @param userId ID del usuario
     * @return número de mensajes no leídos
     */
    public long getUnreadCount(Long userId) {
        Usuario user = usuarioRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return messageRepository.countByRecipientAndIsReadFalse(user);
    }
    
    /**
     * Convierte una entidad ChatMessage a DTO.
     * 
     * @param message entidad mensaje
     * @return DTO del mensaje
     */
    private ChatMessageDto toDto(ChatMessage message) {
        return ChatMessageDto.builder()
            .id(message.getId())
            .senderId(message.getSender().getId())
            .senderName(message.getSender().getNombre())
            .recipientId(message.getRecipient().getId())
            .recipientName(message.getRecipient().getNombre())
            .content(message.getContent())
            .timestamp(message.getTimestamp())
            .isRead(message.isRead())
            .messageType(message.getMessageType())
            .build();
    }
}