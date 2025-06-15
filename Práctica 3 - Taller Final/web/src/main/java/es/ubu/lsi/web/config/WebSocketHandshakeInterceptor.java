package es.ubu.lsi.web.config;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpSession;

/**
 * Interceptor para el handshake de WebSocket.
 * Extrae la información de la sesión HTTP y la hace disponible para WebSocket.
 * 
 * @author Ibai Moya Aroz
 * @version 1.0
 * @since 1.0
 */
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    /**
     * Intercepta el handshake antes de establecer la conexión WebSocket.
     * Copia atributos de la sesión HTTP a los atributos de WebSocket.
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession(false);
            
            if (session != null) {
                // Copiar el ID de sesión
                attributes.put("sessionId", session.getId());
                
                // Obtener el usuario autenticado
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                    attributes.put("username", auth.getName());
                }
            }
        }
        
        return true;
    }

    /**
     * Se ejecuta después del handshake.
     * No realizamos ninguna acción adicional aquí.
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                               WebSocketHandler wsHandler, Exception exception) {
    }
}