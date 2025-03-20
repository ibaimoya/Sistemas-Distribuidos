package es.ubu.lsi.client;


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
    private int DEFAULT_PORT = 1500;

    /** Servidor al que se conectará el cliente. */
    private String server;

    /** Puerto de conexión con el servidor. */
    private String username;

    /** Nombre de usuario del cliente. */
    private int port;

    /** Flag para mantener la conexión. */
    private boolean carryOn = true;

    /** Identificador del cliente. */
    private int id;


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