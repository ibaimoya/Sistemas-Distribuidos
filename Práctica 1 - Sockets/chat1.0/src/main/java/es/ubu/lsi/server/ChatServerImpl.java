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
import es.ubu.lsi.common.ChatMessage.MessageType;

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
    
    /** Mapa de clientes conectados al servidor. 
     * La key serán los nombres de usuario.
     * En la posición 0 del array estará la posición,
     * en la 1 el socket, en la 2 la fecha de creación
     * y en la 3 la lista de usuarios vetados.
    */
    private final Map<String, ArrayList<Object>> clientsMap = new HashMap<>();

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
            System.out.printf(ChatServerImpl.YELLOW +  "+-----------------------------------------------------------------------------+\n"
                                + "[*] " + ChatServerImpl.CYAN + "Iniciando servidor en el puerto" + 
                                ChatServerImpl.GREEN + " %d" + ChatServerImpl.CYAN + "...\n" + ChatServerImpl.RESET, port); 
                                this.generalSocket = new ServerSocket(this.port);
            
            while(this.alive){
                Socket socket = this.generalSocket.accept();  

                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                String username = input.readLine();

                if (clientsMap.containsKey(username)){
                   System.err.println(ChatServerImpl.RED + "[!] Este cliente ya está conectado.\n" + ChatServerImpl.RESET);
                   socket.close();

                } else {
                    ArrayList<Object> clientData = new ArrayList<>();
                    int id = this.clientId;

                    clientData.add(id);
                    clientData.add(socket);
                    clientData.add(sdf.format(new Date()));

                    ArrayList<String> bannedUsersList = new ArrayList<>();

                    clientData.add(bannedUsersList);

                    clientsMap.put(username, clientData);
                    this.clientId++;

                    ServerThreadForClient clientThread = new ServerThreadForClient(id, username);
                    clientThread.start();

                    String welcomeMsg = ChatServerImpl.YELLOW +"[*] " + ChatServerImpl.CYAN + "El usuario " +
                                        ChatServerImpl.GREEN + username + ChatServerImpl.CYAN + " se ha unido al chat."
                                        + ChatServerImpl.RESET;

                    /* Se anuncia que hay un nuevo usuario. */
                    System.out.println(welcomeMsg);
                    broadcast(new ChatMessage(id, MessageType.MESSAGE, welcomeMsg));
                }
            }
            } catch (IOException ioException) {
                this.alive = false;
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
            System.out.printf(ChatServerImpl.YELLOW + "[*] " + ChatServerImpl.CYAN + "¡Servidor cerrado correctamente!\n");
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
            String originalMessage = message.getMessage();
            String messageToSend;

            if(message.getType() != ChatMessage.MessageType.LOGOUT 
                     && message.getType() != ChatMessage.MessageType.SHUTDOWN 
                            && !originalMessage.contains("se ha unido al chat.")){
            
            messageToSend = ChatServerImpl.YELLOW + "[*] " + ChatServerImpl.CYAN + "Mensaje de " + ChatServerImpl.GREEN 
                                + sourceUsername + ChatServerImpl.CYAN + " [" + time + "]: " + ChatServerImpl.RESET 
                                + originalMessage + "\n" ;
            
            } else {
                messageToSend = originalMessage;
            }
            for (Map.Entry<String, ArrayList<Object>> user : clientsMap.entrySet()) {

                String recipient = user.getKey();

                if ((int) user.getValue().get(0) != sourceId) {
                    @SuppressWarnings("unchecked")
                    ArrayList<String> recipientBanned = (ArrayList<String>) clientsMap.get(recipient).get(3);

                    if (!recipientBanned.contains(sourceUsername)) {
                        try {
                            PrintWriter output = new PrintWriter(((Socket) user.getValue().get(1)).getOutputStream(), true);
                            output.println(messageToSend);
                        } catch (IOException ioException) {
                            System.err.printf(ChatServerImpl.RED + "[!] Error al enviar el mensaje a " + ChatServerImpl.GREEN 
                                                + recipient + ChatServerImpl.RESET + ": %s\n", ioException.getMessage());
                        }
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
                                + username + ChatServerImpl.CYAN + " ha salido del servidor.\n" + ChatServerImpl.RESET);
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
        private final int id;

        /* Nombre de usuario del cliente. */
        private final String username;

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

        /** Mensaje de shudown. */
        private static final String SHUTDOWN = "SHUTDOWN";


        /**
         * Constructor de ServerThreadForClient.
         * 
         * @param id Identificador del cliente
         * @param username Nombre de usuario del cliente
         */
        public ServerThreadForClient(int id, String username) {
            this.id = id;
            this.username = username;  
            Socket clientSocket = (Socket) clientsMap.get(this.username).get(1);

            try {

                this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                this.output = new PrintWriter(clientSocket.getOutputStream(), true);

                output.println(ChatServerImpl.YELLOW + "+-----------------------------------------------------------------------------+\n"  
                                    + "[*] " + ChatServerImpl.CYAN + "¡Bienvenido al chat, " + ChatServerImpl.GREEN 
                                    + username + ChatServerImpl.CYAN + "!" + ChatServerImpl.RESET);

            } catch (IOException ioException) {
                System.err.printf(ChatServerImpl.RED + "[!] Error al tratar de inicializar el hilo para el cliente: " + ChatServerImpl.RESET + "%s\n", ioException.getMessage());
            }
        }

        @Override
        public void run() {

            while (alive) {
                try {
                    String message = input.readLine();
                    if (message == null) {
                        break;
                    }
                    String[] words = message.split(" ");
                  
                    switch (words[0].toUpperCase()) {
                        case ServerThreadForClient.LOGOUT:
                            logoutCase();
                            break;

                        case ServerThreadForClient.BAN:
                            banCase(words);
                            break;

                        case ServerThreadForClient.PARDON: 
                            pardonCase(words);
                            break;

                        case ServerThreadForClient.SHUTDOWN:
                            shutdownCase();
                            break;
                        
                        default:
                            if (!message.isEmpty() && !message.equals("\n")) {
                                ChatMessage chatMessage = new ChatMessage(this.id, ChatMessage.MessageType.MESSAGE, message);
                                broadcast(chatMessage);   
                            }
                            break;
                    }
                } catch (IOException ioException) {
                    break;
                }
            }
        }

        /**
         * Caso del logout.
         * 
         * @param serverMessage Mensaje informativo
         */
        private void logoutCase(){
            String serverMessage = ChatServerImpl.YELLOW +"[*] " + ChatServerImpl.CYAN + "El usuario " +
            ChatServerImpl.GREEN + this.username + ChatServerImpl.CYAN + " ha abandonado el chat."
            + ChatServerImpl.RESET;

            ChatMessage logoutMessage = new ChatMessage(this.id, ChatMessage.MessageType.LOGOUT, serverMessage);
            broadcast(logoutMessage);
            remove(this.id);
        }

        /**
         * Caso del veto.
         * 
         * @param serverMessage Mensaje informativo
         */
        private void banCase(String[] words){
            if (words.length != 2){
                output.println(ChatServerImpl.RED + "[!] Debes indicar el nombre de usuario a banear. " + ChatServerImpl.RESET
                               + ChatServerImpl.YELLOW + "[*] " + ChatServerImpl.CYAN + "Uso: ban <nombre_usuario>" + ChatServerImpl.RESET);
            } else{
                banUser(this.id, words[1]);
            }
        }

        /**
         * Caso del indulto.
         * 
         * @param serverMessage Mensaje informativo
         */
        private void pardonCase( String[] words){
            if (words.length != 2){
                output.println(ChatServerImpl.RED + "[!] Debes indicar el nombre de usuario a indultar. " + ChatServerImpl.RESET
                               + ChatServerImpl.YELLOW + "[*] " + ChatServerImpl.CYAN + "Uso: unban <nombre_usuario>" + ChatServerImpl.RESET);
            } else{
                pardonUser(this.id, words[1]);
            }
        }

        /**
         * Caso del indulto.
         * 
         * @param serverMessage Mensaje informativo
         */
        private void shutdownCase(){
            String serverMessage = ChatServerImpl.YELLOW + "[*] " + ChatServerImpl.CYAN + "El servidor se está apagando...\n" + ChatServerImpl.RESET;
            broadcast(new ChatMessage(this.id, ChatMessage.MessageType.SHUTDOWN, serverMessage));
            output.println(serverMessage);

            ChatServerImpl.this.shutdown();
        }
    }    

        /**
         * Método que permite vetar a un usuario siendo otro usuario.
         * 
         * @param idSource Identificador del usuario que banea
         * @param bannedUsername Nombre de usuario del usuario baneado
         */
        @SuppressWarnings("unchecked")
        private void banUser(int idSource, String bannedUsername){
            PrintWriter clientOutput;
            try{

                String sourceUsername = findUserById(idSource); 
                Socket socketCliente = (Socket) clientsMap.get(sourceUsername).get(1);


                clientOutput = new PrintWriter(socketCliente.getOutputStream(), true);

                if (bannedUsername == null) return;

                if(sourceUsername.equals(bannedUsername)){
                    clientOutput.println(ChatServerImpl.RED + "[!] ¡No te puedes vetar a ti mismo!" + ChatServerImpl.RESET);
                }else{

                    ((ArrayList<String>) clientsMap.get(sourceUsername).get(3)).add(bannedUsername);

                    String banMessage= ChatServerImpl.YELLOW + "[*] " + ChatServerImpl.CYAN + "El usuario " + ChatServerImpl.GREEN 
                                        + sourceUsername + ChatServerImpl.CYAN + " ha vetado a " + ChatServerImpl.GREEN
                                        + bannedUsername + ChatServerImpl.CYAN + ".\n" + ChatServerImpl.RESET;
                                        
                    /* Se difunde el mensaje a todos los clientes. */
                    broadcast(new ChatMessage(idSource, ChatMessage.MessageType.MESSAGE, banMessage));

                    /* Se informa al cliente que ha ejecutado el indulto. */
                    clientOutput.println(ChatServerImpl.YELLOW + "[*] " + ChatServerImpl.CYAN + "Has vetado con éxito a "
                                        + ChatServerImpl.GREEN + bannedUsername + ChatServerImpl.CYAN + "."
                                        + ChatServerImpl.RESET + "\n");

                    /* Se muestra en la consola del servidor. */
                    System.out.println(ChatServerImpl.YELLOW + "[*] " + ChatServerImpl.CYAN + "El usuario "
                                        + ChatServerImpl.GREEN + sourceUsername + ChatServerImpl.CYAN + " ha vetado a "
                                        + ChatServerImpl.GREEN + bannedUsername + ChatServerImpl.CYAN + ". "
                                        + ChatServerImpl.RESET + "\n");
                }
            } catch (IOException ioException){
                System.err.printf(ChatServerImpl.RED + "[!] Error al informar del baneo a " + ChatServerImpl.GREEN 
                + bannedUsername + ChatServerImpl.RESET + ": %s\n", ioException.getMessage());
            } 
        }
        
        
        /**
         * Método que indulta a un usuario vetado del chat de la 
         * lista de vetados de otro usuario.
         * 
         * @param idSource Identificador del usuario que perdona
         * @param pardonedUsername Nombre de usuario del usuario perdonado
         */
        @SuppressWarnings("unchecked")
        private void pardonUser(int idSource, String pardonedUsername) {
            PrintWriter clientOutput;
            try {
                String sourceUsername = findUserById(idSource);
                Socket socketCliente = (Socket) clientsMap.get(sourceUsername).get(1);
                clientOutput = new PrintWriter(socketCliente.getOutputStream(), true);
                        
                if (pardonedUsername == null) return;

                if (sourceUsername.equals(pardonedUsername)) {
                    clientOutput.println(ChatServerImpl.RED + "[!] ¡No te puedes quitar el veto a ti mismo!" + ChatServerImpl.RESET);
                } else {
                    /* Obtiene la lista de usuarios vetados del usuario que ejecuta la acción. */
                    ArrayList<String> bannedList = (ArrayList<String>) clientsMap.get(sourceUsername).get(3);
                    
                    if (!bannedList.contains(pardonedUsername)) {
                        /* Si el usuario no está baneado, se informa al cliente. */
                        clientOutput.println(ChatServerImpl.RED + "[!] El usuario " + ChatServerImpl.GREEN 
                                                + pardonedUsername + ChatServerImpl.RED + " no está vetado." 
                                                + ChatServerImpl.RESET);
                    } else {
                        /* El usuario está baneado, se procede a quitar el veto. */
                        bannedList.remove(pardonedUsername);
                        
                        String pardonMsg = ChatServerImpl.YELLOW + "[*] " + ChatServerImpl.CYAN + "El usuario " 
                                                + ChatServerImpl.GREEN + sourceUsername + ChatServerImpl.CYAN 
                                                + " ha quitado su veto a " + ChatServerImpl.GREEN + pardonedUsername 
                                                + ChatServerImpl.CYAN + ".\n" + ChatServerImpl.RESET;
                        
                        /* Se difunde el mensaje a todos los clientes. */
                        broadcast(new ChatMessage(idSource, ChatMessage.MessageType.MESSAGE, pardonMsg));
                        
                        /* Se informa al cliente que ha ejecutado el indulto. */
                        clientOutput.println(ChatServerImpl.YELLOW + "[*] " + ChatServerImpl.CYAN + "Has quitado el veto a "
                                                + ChatServerImpl.GREEN + pardonedUsername + ChatServerImpl.CYAN + ".\n" 
                                                + ChatServerImpl.RESET);
                        
                        /* Se muestra en la consola del servidor. */
                        System.out.println(pardonMsg);
                    }
                }
            } catch (IOException ioException) {
                System.err.printf(ChatServerImpl.RED + "[!] Error al informar el indulto a " + ChatServerImpl.GREEN 
                                    + pardonedUsername + ChatServerImpl.RESET + ": %s\n", ioException.getMessage());
            } 
        }
}