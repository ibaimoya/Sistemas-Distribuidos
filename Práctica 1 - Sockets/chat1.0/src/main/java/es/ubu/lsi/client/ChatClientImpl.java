package es.ubu.lsi.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import es.ubu.lsi.common.ChatMessage;
import es.ubu.lsi.common.ChatMessage.MessageType;

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
    private static final int DEFAULT_PORT = 1500;

    /** Mensaje de logout. */
    private static final String LOGOUT = "logout";
    
    /** Mensaje de shudown. */
    private static final String SHUTDOWN = "shutdown";

    /** Mensaje de mensaje. */
    private static final String MESSAGE = "message";

    /** Servidor al que se conectará el cliente. */
    private String server;

    /** Puerto de conexión con el servidor. */
    private String username;

    /** Nombre de usuario del cliente. */
    private static int port = ChatClientImpl.DEFAULT_PORT;

    /** Flag para mantener la conexión. */
    private boolean carryOn = true;

    /** Identificador del cliente. */
    private int id;


    private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	

    /** Color rojo. */
    private static final String RED = "\u001B[31m";

    /** Color amarillo. */
    private static final String YELLOW = "\u001B[33m";

    /** Color magenta. */
    private static final String MAGENTA = "\u001B[35m";

    /** Color cian. */
    private static final String CYAN = "\u001B[36m";

    /** Reset de color. */
    private static final String RESET = "\u001B[0m";

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
            } catch (ClassNotFoundException CNFException) {

                System.err.printf(ChatClientImpl.RED + "[!] Error de clase no encontrada: "  + ChatClientImpl.RESET + "%s\n", CNFException.getMessage());
            } catch (IOException ioException) {

                System.err.printf(ChatClientImpl.RED + "[!] Error de entrada / salida: " + ChatClientImpl.RESET + "%s\n", ioException.getMessage());
            }
        }
    }

    @Override
    public boolean start() {
        boolean success;

        try {
            /* Se inicializan los elementos necesarios para la conexión. */
            this.socket = new Socket(this.server, this.port);
            this.output = new ObjectOutputStream(this.socket.getOutputStream());
            this.input = new ObjectInputStream(this.socket.getInputStream());

            /* Se inicializa el hilo que escucha los mensajes del servidor. */
            ChatClientListener clientListener = new ChatClientListener();
            new Thread(clientListener).start();

            success = true;
            
        } catch (IOException ioException) {
            success = false;

            System.err.printf(ChatClientImpl.RED + "[!] Error al tratar de inicializar el cliente: " + ChatClientImpl.RESET + "%s\n", ioException.getMessage());
        } 

        return success;
    }

    @Override
    public void sendMessage(ChatMessage message) {
        try {

            /* Se escribe el mensaje en el flujo de salida. */
            output.writeObject(message);
        } catch (IOException ioException) {
            System.err.printf(ChatClientImpl.RED + "[!] Error de entrada / salida: " + ChatClientImpl.RESET + "%s\n", ioException.getMessage());
        }
    }

    @Override
    public void disconnect() {
        try {
            System.out.println(ChatClientImpl.YELLOW + "[*]" + ChatClientImpl.CYAN + " Desconectando del sistema..." + ChatClientImpl.RESET + "\n");
            carryOn = false;

            /* Se cierran los elementos de la conexión. */
            if (socket != null){
                socket.close();
            }

            if (input != null){
                input.close();
            }

            if (output != null){
                output.close();
            }

        } catch (IOException ioException) {
            System.err.printf(ChatClientImpl.RED + "[!] Error al cerrar los elementos: " + ChatClientImpl.RESET + "%s\n", ioException.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.printf(ChatClientImpl.RED + "[!] Error en el formato de entrada.\n" + ChatClientImpl.RESET);
            System.exit(1);
        }
		
		String server = args[0];
	    port = Integer.parseInt(args[1]);
	    String username = args[2];
	    
	    ChatClientImpl cliente = new ChatClientImpl(server, port, username);
	    
	    if (!cliente.start()) {
            System.err.printf(ChatClientImpl.RED + "[!] ERROR: El cliente no se ha inicializado correctamente.\n" + ChatClientImpl.RESET);
            System.exit(1);


	    } else {
	    	Scanner scanner = new Scanner(System.in);
	    	
	    	while (cliente.carryOn) {
	    		String input = scanner.nextLine();
	    		
                switch (input.toUpperCase()) {
                    case "LOGOUT":
                        String logoutText = cliente.username + " patrocina el mensaje: logout";
                        ChatMessage logoutMsg = new ChatMessage(cliente.id, MessageType.LOGOUT, logoutText);
                        cliente.sendMessage(logoutMsg);
                        cliente.carryOn = false;
                        break;
                    case "SHUTDOWN":
                        String shutdownText = cliente.username + " patrocina el mensaje: shutdown";
                        ChatMessage shutdownMsg = new ChatMessage(cliente.id, MessageType.SHUTDOWN, shutdownText);
                        cliente.sendMessage(shutdownMsg);
                        cliente.carryOn = false;
                        break;
                    case "MESSAGE":
                        String messageText = cliente.username + " patrocina el mensaje: " + input;
                        ChatMessage message = new ChatMessage(cliente.id, MessageType.MESSAGE, messageText);
                        cliente.sendMessage(message);
                        break;
                    default:
                        System.err.printf(ChatClientImpl.RED + "[!] ERROR: No se reconoce el tipo de mensaje.\n" + ChatClientImpl.RESET);
                        break;
                }
            }
            scanner.close();
            cliente.disconnect();
        }
	}
}