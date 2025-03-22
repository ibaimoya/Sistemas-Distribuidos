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
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    /** Puerto de conexión con el servidor guardado en una variable . */
    private int port = ChatServerImpl.DEFAULT_PORT;

    /** Identificador del cliente. */
    private int clientId = 0;

    /** Flag para mantener la conexión. */
    private boolean alive = true;
    
    /** Mapa de clientes conectados al servidor. */
    private Map<String, ArrayList<Object>> clientsMap = new HashMap<>();

    /** Socket general del servidor. */
    private ServerSocket generalSocket = null;

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
     * Constructor de ChatServerImpl.
     */
    public ChatServerImpl(int port) {
        this.port = port;
    }

    @Override
    public void startup() {


        try{
            this.alive = true;
            System.out.printf(ChatServerImpl.YELLOW + "[*] " + ChatServerImpl.CYAN + "Iniciando servidor en el puerto" + 
                                ChatServerImpl.GREEN + " %d\n" + ChatServerImpl.CYAN + "...\n" + ChatServerImpl.RESET, port); 
                                this.generalSocket = new ServerSocket(this.port);

            Socket socket;
            
            while(this.alive){
                socket = this.generalSocket.accept();  

                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                String username = input.readLine();

                if (clientsMap.containsKey(username)){
                   System.err.println(ChatServerImpl.RED + "[!] Este cliente ya está conectado.\n" + ChatServerImpl.RESET);
                   socket.close();

                } else {
                    ArrayList<Object> clientData = new ArrayList<>();
                    clientData.add(this.clientId);
                    clientData.add(socket);
                    clientData.add(sdf.format(new Date()));

                    ArrayList<String> bannedUsersList = new ArrayList<>();

                    clientData.add(bannedUsersList);

                    clientsMap.put(username, clientData);
                    this.clientId++;

                    ServerThreadForClient clientThread = new ServerThreadForClient(clientId, username);
                    clientThread.start();
                }
            }

        } catch (IOException ioException) {
            System.err.printf(ChatServerImpl.RED + "[!] Error al iniciar el servidor: " + ChatServerImpl.RESET + "%s\n", ioException.getMessage());
            this.alive = false;
        } finally {
            System.out.printf(ChatServerImpl.YELLOW + "[*] " + ChatServerImpl.CYAN + "Cerrando el servidor...\n" + ChatServerImpl.RESET);
            if (this.generalSocket != null) {
                try {
                    this.generalSocket.close();
                } catch (IOException ioException) {
                    System.err.printf(ChatServerImpl.RED + "[!] Error al cerrar el socket del servidor: " + ChatServerImpl.RESET + "%s\n", ioException.getMessage());
                }
            }
            System.exit(1);
        }
    }

    @Override
    public void shutdown() {
        this.alive = false;

        /* 0 -> no hay errores, 1 -> ha ocurrido un error. */
        int successState = 0;

        System.out.printf(ChatServerImpl.YELLOW + "[*] " + ChatServerImpl.CYAN + "Cerrando el servidor...\n");

        /* Se cierran los sockets de los clientes. */
        for (Map.Entry<String, ArrayList<Object>> user : clientsMap.entrySet()) {
            try {
                ((Socket) user.getValue().get(1)).close();
            } catch (IOException ioException) {
                System.err.printf(ChatServerImpl.RED + "[!] Error al cerrar el socket del cliente: " + ChatServerImpl.RESET + "%s\n", ioException.getMessage());
                successState = 1;
            }
        }

        /* Cierra el socket del servidor. */
        if (this.generalSocket != null) {
            try {
                this.generalSocket.close();
            } catch (IOException ioException) {
                System.err.printf(ChatServerImpl.RED + "[!] Error al cerrar el socket del servidor: " + ChatServerImpl.RESET + "%s\n", ioException.getMessage());
                successState = 1;
            }
        }

        if (successState == 0) {
            System.out.printf(ChatServerImpl.GREEN + "[*] " + ChatServerImpl.CYAN + "Servidor cerrado correctamente.\n");
        } else {
            System.err.printf(ChatServerImpl.RED + "[!] Ha ocurrido un error al cerrar el servidor.\n");
        }   
        System.out.printf(ChatServerImpl.RESET);
        System.exit(successState);
    }

    @Override
    public void broadcast(ChatMessage message) {
        int sourceId = message.getId();
        String sourceUsername = findUserById(sourceId);
        
        String time = sdf.format(new Date());

        if (sourceUsername != null) {
            String messageToSend = ChatServerImpl.YELLOW + "[*] " + ChatServerImpl.CYAN + "Mensaje de " + ChatServerImpl.GREEN 
                                + sourceUsername + " [" + time + "]: " + ChatServerImpl.CYAN + ": " + ChatServerImpl.RESET 
                                + message.getMessage() + "\n" ;

            for (Map.Entry<String, ArrayList<Object>> user : clientsMap.entrySet()) {
                if ((int) user.getValue().get(0) != sourceId) {
                    try {
                        PrintWriter output = new PrintWriter(((Socket) user.getValue().get(1)).getOutputStream(), true);
                        output.println(messageToSend);
                    } catch (IOException ioException) {
                        System.err.printf(ChatServerImpl.RED + "[!] Error al enviar el mensaje a " + ChatServerImpl.GREEN 
                                            + user.getKey() + ChatServerImpl.RESET + ": %s\n", ioException.getMessage());
                    }
                }
            }
        } else {
            System.err.printf(ChatServerImpl.RED + "[!] No se ha encontrado el cliente con identificador %d.\n" + ChatServerImpl.RESET, sourceId);
        }
    }

    @Override
    public void remove(int id) {
        String username = findUserById(id);
        if (username != null) {
            clientsMap.remove(username);
            System.out.printf(ChatServerImpl.YELLOW + "[*] " + ChatServerImpl.CYAN + "El cliente " + ChatServerImpl.GREEN 
                                + username + ChatServerImpl.CYAN + " ha sido eliminado del servidor.\n");
        } else {
            System.err.printf(ChatServerImpl.RED + "[!] No se ha encontrado el cliente con identificador %d.\n" + ChatServerImpl.RESET, id);
        }
    }

    /**
     * Método que busca un usuario por su identificador.
     * 
     * @param id Identificador del usuario
     * @return Nombre de usuario del cliente
     */
    private String findUserById(int id) {
        for (Map.Entry<String, ArrayList<Object>> user : clientsMap.entrySet()) {
            if ((int) user.getValue().get(0) == id) {
                return user.getKey();
            }
        }
        return null;
    }

    public static void main(String[] args) {

        int port = args.length > 0 ? Integer.parseInt(args[0]) : ChatServerImpl.DEFAULT_PORT;

      

        ChatServerImpl server = new ChatServerImpl(port);
        server.startup();

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
        
        /** Sintaxis del comando logout. */
        private static final String LOGOUT = "LOGOUT";

        /** Sintaxis del comando ban. */
        private static final String BAN = "BAN";

        /** Sintaxis del comando unban. */
        private static final String PARDON = "UNBAN";


        /**
         * Constructor de ServerThreadForClient.
         * 
         * @param id Identificador del cliente
         * @param username Nombre de usuario del cliente
         */
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

            while (alive) {
                try {
                    String message = input.readLine();
                    String[] words = message.split(" ");
                    String serverMessage;
                  
                    switch (words[0].toUpperCase()) {
                        case ServerThreadForClient.LOGOUT:
                            serverMessage = ChatServerImpl.YELLOW +"[*] " + ChatServerImpl.CYAN + "El usuario " +
                                      ChatServerImpl.GREEN + this.username + ChatServerImpl.CYAN + " ha abandonado el chat."
                                      + ChatServerImpl.RESET;

                            ChatMessage logoutMessage = new ChatMessage(this.id, ChatMessage.MessageType.LOGOUT, serverMessage);
                            broadcast(logoutMessage);
                            remove(this.id);
                            break;

                        case ServerThreadForClient.BAN:
                            if (words.length != 2){
                                output.println(ChatServerImpl.RED + "[!] Debes indicar el nombre de usuario a banear.\n" + ChatServerImpl.RESET);
                                output.println(ChatServerImpl.YELLOW + "[*] " + ChatServerImpl.CYAN + "Uso: ban <nombre_usuario>\n" + ChatServerImpl.RESET);
                                break;
                            }

                            banUser(this.id, words[1]);
                            break;

                        case ServerThreadForClient.PARDON: 
                            if (words.length != 2){
                                output.println(ChatServerImpl.RED + "[!] Debes indicar el nombre de usuario a perdonar.\n" + ChatServerImpl.RESET);
                                output.println(ChatServerImpl.YELLOW + "[*] " + ChatServerImpl.CYAN + "Uso: unban <nombre_usuario>\n" + ChatServerImpl.RESET);
                                break;
                            }
                            
                            pardonUser(this.id, words[1]);
                            break;

                        default:
                            if (!message.isEmpty() && !message.equals("\n")) {
                                ChatMessage chatMessage = new ChatMessage(this.id, ChatMessage.MessageType.MESSAGE, message);
                                broadcast(chatMessage);   
                            }
                            break;
                    }
                } catch (IOException ioException) {
                    System.err.printf(ChatServerImpl.RED + "[!] Error al recibir el mensaje del cliente " + ChatServerImpl.GREEN 
                                        + this.username + ChatServerImpl.RESET + ": %s\n", ioException.getMessage());
                    break;
                }
            }
        }
        
        /**
         * Método que permite vetar a un usuario siendo otro usuario.
         * 
         * @param idSource Identificador del usuario que banea
         * @param bannedUsername Nombre de usuario del usuario baneado
         */
        private void banUser(int idSource, String bannedUsername){
            String sourceUsername = findUserById(idSource); 

            ((ArrayList<String>) clientsMap.get(sourceUsername).get(3)).add(bannedUsername);

            String banMessage= ChatServerImpl.YELLOW + "[*] " + ChatServerImpl.CYAN + "El usuario " + ChatServerImpl.GREEN 
                                + sourceUsername + ChatServerImpl.CYAN + " ha baneado a " + ChatServerImpl.GREEN
                                + bannedUsername + ChatServerImpl.CYAN + " del chat." + ChatServerImpl.RESET;

            broadcast(new ChatMessage(idSource, ChatMessage.MessageType.MESSAGE, banMessage));

        }
        
        
        /**
         * Método que indulta a un usuario vetado del chat de la 
         * lista de vetados de otro usuario.
         * 
         * @param idSource Identificador del usuario que perdona
         * @param pardonedUsername Nombre de usuario del usuario perdonado
         */
        private void pardonUser(int idSource, String pardonedUsername){
            String sourceUsername = findUserById(idSource); 

            ((ArrayList<String>) clientsMap.get(sourceUsername).get(3)).remove(pardonedUsername);

            String pardonMessage= ChatServerImpl.YELLOW + "[*] " + ChatServerImpl.CYAN + "El usuario " + ChatServerImpl.GREEN 
                                + sourceUsername + ChatServerImpl.CYAN + " ha quitado su veto a " + ChatServerImpl.GREEN
                                + pardonedUsername + ChatServerImpl.CYAN + " ." + ChatServerImpl.RESET;

            broadcast(new ChatMessage(idSource, ChatMessage.MessageType.MESSAGE, pardonMessage));
        }
    }   
}
