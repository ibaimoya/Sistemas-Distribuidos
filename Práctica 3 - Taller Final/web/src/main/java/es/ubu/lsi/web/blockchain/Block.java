package es.ubu.lsi.web.blockchain;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

/**
 * Representa un bloque en la blockchain.
 * Cada bloque contiene datos de valoración y está enlazado al bloque anterior.
 * 
 * @author Ibai Moya Aroz
 * 
 * @version 1.0
 * @since 1.0
 */
@Getter
@Setter
public class Block {
    
    /** Índice del bloque en la cadena. */
    private int index;
    
    /** Timestamp de cuando se creó el bloque. */
    private long timestamp;
    
    /** Datos de la valoración (formato: usuarioId:movieId:rating). */
    private String data;
    
    /** Hash del bloque anterior */
    private String previousHash;
    
    /** Hash de este bloque. */
    private String hash;
    
    /** Número aleatorio que se incrementa para encontrar un hash válido (proof of work). */
    private int nonce;
    
    /**
     * Constructor del bloque.
     * 
     * @param index índice del bloque
     * @param data datos de la valoración
     * @param previousHash hash del bloque anterior
     */
    public Block(int index, String data, String previousHash) {
        this.index = index;
        this.timestamp = Instant.now().toEpochMilli();
        this.data = data;
        this.previousHash = previousHash;
        this.nonce = 0;
        this.hash = calculateHash();
    }
    
    /**
     * Calcula el hash del bloque usando SHA-256.
     * 
     * @return el hash calculado
     */
    public String calculateHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = index + timestamp + data + previousHash + nonce;
            byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));
            
            /* Convertir bytes a hexadecimal. */
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Error calculando hash:", e);
        }
    }
    
    /**
     * Mina el bloque hasta encontrar un hash que cumpla con la dificultad.
     * 
     * @param difficulty número de ceros que debe tener el hash al principio
     */
    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
    }
}