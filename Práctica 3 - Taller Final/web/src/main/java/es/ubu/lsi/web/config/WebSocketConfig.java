package es.ubu.lsi.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuración de WebSocket para el chat en tiempo real.
 * Configura STOMP sobre WebSocket para mensajería bidireccional.
 * 
 * @author Ibai Moya Aroz
 * 
 * @version 1.0
 * @since 1.0
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configura el broker de mensajes.
     * Define los prefijos para los destinos de la aplicación y del broker.
     * 
     * @param config el registro de configuración del broker
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita un broker simple en memoria con prefijos para topics y colas
        config.enableSimpleBroker("/topic", "/queue");
        // Prefijo para los destinos manejados por @MessageMapping
        config.setApplicationDestinationPrefixes("/app");
        // Prefijo para mensajes dirigidos a usuarios específicos
        config.setUserDestinationPrefix("/user");
    }

    /**
     * Registra los endpoints STOMP.
     * Define el punto de conexión WebSocket y habilita SockJS como fallback.
     * 
     * @param registry el registro de endpoints STOMP
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint para la conexión WebSocket
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new WebSocketHandshakeInterceptor())
                .withSockJS(); // Fallback para navegadores que no soportan WebSocket
    }
}