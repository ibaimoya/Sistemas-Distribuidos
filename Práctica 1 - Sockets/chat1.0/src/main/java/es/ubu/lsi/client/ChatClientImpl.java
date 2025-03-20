package es.ubu.lsi.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import es.ubu.lsi.common.ChatMessage;

/**
 * Implementación de la interfaz ChatClient.
 * 
 * @version 1.0
 * @since 1.0
 * 
 * @author Ibai Moya Aroz
 * 
 * @see ChatClient
 */
public class ChatClientImpl implements ChatClient {

    /** Puerto por defecto. */
    private final int DEFAULT_PORT = 1500;

    /** Servidor al que se conectará el cliente. */
    private String server;

    /** Puerto de conexión con el servidor. */
    private String username;

    /** Nombre de usuario del cliente. */
    private int port = DEFAULT_PORT;

    /** Flag para mantener la conexión. */
    private boolean carryOn = true;

    /** Identificador del cliente. */
    private int id;


    private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	

    /** Color amarillo. */
    String yellow = "\u001B[33m";

    /** Color cian. */
    String cyan = "\u001B[36m";

    /** Reset de color. */
    String reset = "\u001B[0m";

    /**
     * Constructor de ChatClientImpl.
     * 
     * @param server El servidor al que se conecta el cliente
     * @param port El puerto de conexión con el servidor
     * @param username El nombre de usuario del cliente
     */
    public ChatClientImpl(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.username = username;
    }

    /**
     * Clase interna que implementa la interfaz Runnable para escuchar los mensajes del servidor.
     *  
     * @version 1.0
     * @since 1.0
     * 
     * @author Ibai Moya Aroz
     * 
     * @see Runnable
     */
    public class ChatClientListener implements Runnable {

        @Override
        public void run() {
            try {
                /* Mientras el cliente esté activo (carryOn es true), se espera recibir mensajes. */
                while (carryOn) {
                    
                    /* Lee un objeto del flujo de entrada. */
                    Object receivedObject  = input.readObject();
                    
                    /* Verifica si el objeto recibido es de tipo ChatMessage 
                    *  para transformarlo si es necesario. */
                    if (receivedObject  instanceof ChatMessage) {

                        ChatMessage receivedMsg = (ChatMessage) receivedObject ;

                        System.out.println(receivedMsg.getId() + ": " + receivedMsg.getMessage());
                    }
                }
            } catch (IOException | ClassNotFoundException exception) {
                // En caso de error (por ejemplo, problemas al leer el objeto), se muestra el mensaje de error.
                System.err.printf("Error in listener thread: %s%n", exception.getMessage());
            }
    }
    }

    @Override
    public boolean start() {
       // ChatClientListener listener = new ChatClientListener(this);
       // new Thread(listener).start();
        return true;
    }

    @Override
    public void sendMessage(String message) {
    }

    @Override
    public void disconnect() {
        System.out.println(this.yellow + "[*]" + this.cyan + " Desconectando del sistema..." + this.reset + "\n");
        carryOn = false;
    }

    public static void main(String[] args) {
        
    }
}