package es.ubu.lsi.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

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
 * @see es.ubu.lsi.client.ChatClient
 * @see es.ubu.lsi.common.ChatMessage
 * @see es.ubu.lsi.common.ChatMessage.MessageType
 */
public class ChatClientImpl implements ChatClient {

    
    /** Servidor por defecto. */
    private static final String DEFAULT_SERVER = "localhost";

    /** Puerto por defecto. */
    private static final int DEFAULT_PORT = 1500;

    /** Usuario por defecto. */
    private static final String DEFAULT_USER = "Usuario";

    /** Mensaje de logout. */
    private static final String LOGOUT = "LOGOUT";
    
    /** Mensaje de shudown. */
    private static final String SHUTDOWN = "SHUTDOWN";

    /** Servidor al que se conectará el cliente. */
    private String server = ChatClientImpl.DEFAULT_SERVER;

    /** Puerto de conexión con el servidor. */
    private String username = ChatClientImpl.DEFAULT_USER;

    /** Nombre de usuario del cliente. */
    private int port = ChatClientImpl.DEFAULT_PORT;

    /** Flag para mantener la conexión. */
    private boolean carryOn = true;

    /** Identificador del cliente. */
    private int id;

    /* Elementos de la conexión. */
    private Socket socket;
	
    /** Flujo de entrada actual. */
    private BufferedReader buffer;



    /** Color rojo. */
    private static final String RED = "\u001B[31m";

    /** Color verde. */
    private static final String GREEN = "\u001B[32m";

    /** Color amarillo. */
    private static final String YELLOW = "\u001B[33m";

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
                String line;

                while (carryOn && (line = buffer.readLine()) != null) {

                    System.out.println(line);
                }
            
            } catch (IOException ioException) {

                if (carryOn) {
                    System.err.printf(ChatClientImpl.RED + "[!] Error de conexión con el servidor: " + ChatClientImpl.RESET + "%s\n", ioException.getMessage());
                }
            } finally {
                try {
                    if (socket != null && !socket.isClosed()) {
                        socket.close();
                    }
                } catch (IOException ioException2) {
                    /* Ignora las excepciones en este caso. */
                }
                System.exit(0);
            }
        }
    }

    @Override
    public boolean start() {

        /* Flujo de entrada. */
        BufferedReader input;
        
        /* True = tarea exitosa, False en caso contrario. */
        boolean success;

        try {
            /* Se inicializan los elementos necesarios para la conexión. */
            this.socket = new Socket(this.server, this.port);

            /* Se envía el nombre de usuario al servidor. */
            PrintWriter output = new PrintWriter(this.socket.getOutputStream(), true);
            output.println(this.username);

            input = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), StandardCharsets.UTF_8));

            /* Se inicializa el hilo que escucha los mensajes del servidor. */
            this.buffer = input;
            ChatClientListener clientListener = new ChatClientListener();
            Thread thread = new Thread(clientListener);
            thread.setDaemon(true);
            thread.start();

            checkMsgType(new BufferedReader(new InputStreamReader(System.in)));

            success = true;
            
        } catch (IOException ioException) {
            success = false;

            System.err.printf(ChatClientImpl.RED + "[!] Error al tratar de inicializar el cliente: " + ChatClientImpl.RESET + "%s\n", ioException.getMessage());
            disconnect();
        } 

        return success;
    }

    /** 
     * Método que comprueba el tipo de mensaje recibido y actúa en consecuencia.
     * 
     * @param bufferedInput Flujo de entrada de mensajes.
     */
    private void checkMsgType (BufferedReader bufferedInput) {
        try {
            String message;


            while (this.carryOn) {

                message = bufferedInput.readLine();
                
                switch (message.toUpperCase()) {
                    case ChatClientImpl.LOGOUT:
                        sendMessage(new ChatMessage(this.id, MessageType.LOGOUT, message));
                        disconnect();

                        break;

                    case ChatClientImpl.SHUTDOWN:
                        sendMessage(new ChatMessage(this.id, MessageType.SHUTDOWN, message));
                        break;

                    default:
                        sendMessage(new ChatMessage(this.id, ChatMessage.MessageType.MESSAGE, message));
                        break;
                }
            }

        } catch (IOException ioException) {
            System.err.printf(ChatClientImpl.RED + "[!] Error al interpretar el mensaje: " + ChatClientImpl.RESET + "%s\n", ioException.getMessage());
        } finally {
            try {
                bufferedInput.close();
            } catch (IOException ioException) {
                System.err.printf(ChatClientImpl.RED + "[!] Error al cerrar el flujo de entrada: " + ChatClientImpl.RESET + "%s\n", ioException.getMessage());
                System.exit(1);
            }
        }
    }

    @Override
    public void sendMessage(ChatMessage message) {
        try {
            String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
            String timestamp = ChatClientImpl.CYAN + "[" + time + "]" + ChatClientImpl.RESET;
            
            String messageToSend = message.getMessage();

            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            if (messageToSend.toLowerCase().equals("logout") || messageToSend.toLowerCase().equals("shutdown")) {
                messageToSend = messageToSend.toUpperCase();

                output.println(messageToSend);
                System.out.println(ChatClientImpl.YELLOW + "[*] " + timestamp + ChatClientImpl.GREEN + " " + this.username + ChatClientImpl.CYAN 
                                            + " envía el comando: " + ChatClientImpl.RESET + messageToSend);
            }else if (!messageToSend.equals("") && !messageToSend.equals("\n")) {
            
                output.println(messageToSend);
                System.out.println(ChatClientImpl.YELLOW + "[*] " + timestamp + ChatClientImpl.GREEN + " " + this.username + ChatClientImpl.CYAN 
                                            + " envía el mensaje: " + ChatClientImpl.RESET + messageToSend);    
            }


        } catch (IOException ioException) {
            System.err.printf(ChatClientImpl.RED + "[!] Error al enviar el mensaje: " + ChatClientImpl.RESET + "%s\n", ioException.getMessage());
        }
    }

    @Override
    public void disconnect() {
        System.out.println(ChatClientImpl.YELLOW + "[*]" + ChatClientImpl.CYAN + " Desconectando del sistema..." + ChatClientImpl.RESET + "\n");
        carryOn = false;

        if (this.buffer != null) {
            try {
                this.buffer.close();
            } catch (IOException iOException){
                /* Ignoramos la excepción */
            }
        }
    }

    /**
     * Método principal de la clase ChatClientImpl.
     * 
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        if ((args.length < 3 || args.length > 3) && args.length != 0) {
            System.err.printf(ChatClientImpl.RED + "[!] Error en el formato de entrada ().\n" + ChatClientImpl.RESET);
            System.out.println(ChatClientImpl.YELLOW + "[*] " + ChatClientImpl.CYAN 
                                    + "Uso: \"$java es.ubu.lsi.client.ChatClientImpl <servidor> <puerto> <usuario>\"\n" + ChatClientImpl.RESET);
            System.out.println(ChatClientImpl.YELLOW + "[*] " + ChatClientImpl.CYAN + "En caso de no poner parámetros se usarán los valores por defecto.\n" 
                                    + ChatClientImpl.RESET);
            System.exit(1);
        }
		
		String server = args.length > 0 ? args[0] : ChatClientImpl.DEFAULT_SERVER;
	    int port = args.length > 0 ? Integer.parseInt(args[1]) : ChatClientImpl.DEFAULT_PORT;
	    String username = args.length > 0 ? args[2] : ChatClientImpl.DEFAULT_USER;
	    
	    ChatClientImpl cliente = new ChatClientImpl(server, port, username);
	    
	    if (!cliente.start()) {
            System.err.printf(ChatClientImpl.RED + "[!] ERROR: El cliente no se ha inicializado correctamente.\n" + ChatClientImpl.RESET);
            System.exit(1);

	    } else {
            System.out.println(ChatClientImpl.YELLOW + "[*] " + ChatClientImpl.CYAN + "Cliente inicializado correctamente.\n" + ChatClientImpl.RESET);
        }
	}
}