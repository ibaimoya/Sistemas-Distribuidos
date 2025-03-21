package es.ubu.lsi.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.ubu.lsi.common.ChatMessage;

/**
 *  Implementación de la interfaz ChatServer.
 *  Permite la conexión, envío y recepción de mensajes en el sistema de chat.
 * 
 * @version 1.0
 * @since 1.0
 * 
 * @author Ibai Moya Aroz
 * 
 * @see es.ubu.lsi.server.ChatServer
 * @see es.ubu.lsi.common.ChatMessage
 */
public class ChatServerImpl implements ChatServer {

    /** Puerto por defecto del servidor. */	
    private static final int DEFAULT_PORT = 1500;

    /** Formato de fecha para los mensajes. */
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    /** Puerto de conexión con el servidor guardado en una variable . */
    private int port = ChatServerImpl.DEFAULT_PORT;

    /** Identificador del cliente. */
    private int clientId = 0;

    /** Flag para mantener la conexión. */
    private boolean alive = true;
    
    /** Mapa de clientes conectados al servidor. */
    private Map<String, ArrayList<Object>> clientsMap = new HashMap<>();


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


    @Override
    public void startup() {
        try{
            this.alive = true;
            System.out.printf(ChatServerImpl.GREEN + "[*] Iniciando servidor en el puerto %d\n" + ChatServerImpl.RESET, this.port);

            ServerSocket serverSocket = new ServerSocket(this.port);

            
            while(this.alive){
                Socket socket = serverSocket.accept();  

                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                String username = input.readLine();

                if (clientsMap.containsKey(username)){
                   System.err.println(ChatServerImpl.RED + "[!] Este cliente ya está conectado.");
                   socket.close();

                } else {
                    ArrayList<Object> clientData = new ArrayList<>();
                    clientData.add(this.clientId);
                    clientData.add(socket);
                    clientData.add(sdf.format(new Date()));
                    clientsMap.put(username, clientData);
                    this.clientId++;

                    ServerThreadForClient clientThread = new ServerThreadForClient(clientId, username);
                    clientThread.start();
                }
            }

        } catch (IOException ioException) {
            System.err.printf(ChatServerImpl.RED + "[!] Error al iniciar el servidor: " + ChatServerImpl.RESET + "%s\n", ioException.getMessage());
            this.alive = false;
            System.exit(1);
        }
    }

    @Override
    public void shutdown() {
        // TODO Auto-generated method stub
    }

    @Override
    public void broadcast(ChatMessage message) {
        // TODO Auto-generated method stub
    }

    @Override
    public void remove() {
        // TODO Auto-generated method stub
    }

    /**
     * Clase interna que representa un hilo de ejecución para un cliente,
     * de esta forma permite controlar la comunicación con los clientes.
     */
    private class ServerThreadForClient extends Thread {

        /** Identificador del cliente. */
        private int id;

        /* Nombre de usuario del cliente. */
        private String username;

        /** Socket del cliente. */
        private Socket clientSocket;

        /** Flujo de entrada para el cliente. */
        private BufferedReader input;

        /** Flujo de salida para el cliente. */
        private PrintWriter output;


        public ServerThreadForClient(int id, String username) {
            this.id = id;
            this.username = username;  
            this.clientSocket = (Socket) clientsMap.get(this.username).get(1);

            try {

                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                this.output = new PrintWriter(clientSocket.getOutputStream(), true);

                output.println(ChatServerImpl.YELLOW + "[*] " + ChatServerImpl.CYAN + "¡Bienvenido al chat, " + ChatServerImpl.GREEN 
                                    + username + ChatServerImpl.CYAN + "!");

            } catch (IOException ioException) {
                System.err.printf(ChatServerImpl.RED + "[!] Error al tratar de inicializar el hilo para el cliente: " + ChatServerImpl.RESET + "%s\n", ioException.getMessage());
            }
        }

        @Override
        public void run() {
            // hacer switch con ban, unban...
        }
    }

}