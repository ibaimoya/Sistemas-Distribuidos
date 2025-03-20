package es.ubu.lsi.client;

/**
 * Define la signatura de los métodos de envío de mensaje, desconexión y arranque.
 * 
 * @version 1.0
 * @since 1.0
 * 
 * @author Ibai Moya Aroz
 */
public interface ChatClient {

    /**
     * Inicia la conexión con el servidor.
     * @return true si la conexión se ha realizado con éxito, 
     *         false en caso contrario.
     */
    public boolean start();

    /**
     * Envía un mensaje al servidor.
     * @param message El mensaje a enviar
     */
    public void sendMessage(String message);

    /**
     * Desconecta el cliente del servidor.
     */	
    public void disconnect();
}