package es.ubu.lsi.server;

import es.ubu.lsi.common.ChatMessage;
/**
 * Interfaz que define las operaciones principales para un servidor de chat.
 * Esta implementación del servidor se ejecuta por defecto en el puerto 1500 y no requiere argumentos.
 * Ejemplo de uso: java es.ubu.lsi.server.ChatServerImpl
 * 
 * @version 1.0
 * @since 1.0
 * 
 * @author Ibai Moya Aroz
 * 
 * @see es.ubu.lsi.server.ChatServerImpl
 * @see es.ubu.lsi.common.ChatMessage
 */
public interface ChatServer {

    /**
     * Inicia el servidor e inicializa el hilo principal de ejecución.
     * Crea un ServerSocket que escucha las conexiones entrantes de clientes.
     * Por cada conexión de cliente aceptada, crea y arranca un nuevo ServerThreadForClient
     * manteniendo un registro de todos los hilos creados para la difusión de mensajes y el apagado correcto.
     */
    public void startup();

    /**
     * Detiene el servidor y realiza la limpieza.
     * Cierra todos los flujos de entrada/salida y sockets para cada cliente conectado.
     */
    public void shutdown();

    /**
     * Difunde un mensaje a todos los clientes conectados.
     * Envía el mensaje recibido a los flujos de salida de todos los clientes.
     * 
     * @param message El ChatMessage que será difundido a todos los clientes
     */
    public void broadcast(ChatMessage message);

    /**
     * Elimina un cliente de la lista de clientes conectados al servidor.
     * Se llama cuando un cliente se desconecta o necesita ser eliminado del servidor.
     */
    public void remove(int id);
}