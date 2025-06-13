package es.ubu.lsi.web.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import es.ubu.lsi.web.blockchain.Block;
import es.ubu.lsi.web.blockchain.Blockchain;
import es.ubu.lsi.web.blockchain.BlockchainInfo;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio que gestiona la blockchain de valoraciones.
 * Mantiene una instancia única de la blockchain en memoria.
 * 
 * @author Ibai Moya Aroz
 * 
 * @version 1.0
 * @since 1.0
 */
@Service
@Slf4j
public class BlockchainService {
    
    /** Instancia única de la blockchain. */
    private Blockchain blockchain;
    
    /**
     * Inicializa la blockchain al arrancar el servicio.
     */
    @PostConstruct
    public void init() {
        blockchain = new Blockchain();
        log.info("Blockchain inicializada con bloque génesis");
    }
    
    /**
     * Añade una nueva valoración a la blockchain.
     * 
     * @param userId ID del usuario que valora
     * @param movieId ID de la película valorada
     * @param rating valoración (1-5)
     * @return información del bloque creado
     */
    public Map<String, Object> addRatingToBlockchain(Long userId, Integer movieId, Integer rating) {
        log.info("Añadiendo valoración a blockchain: User={}, Movie={}, Rating={}", 
                 userId, movieId, rating);
        
        Block newBlock = blockchain.addRating(userId, movieId, rating);
        
        Map<String, Object> response = new HashMap<>();
        response.put("blockIndex", newBlock.getIndex());
        response.put("blockHash", newBlock.getHash());
        response.put("timestamp", newBlock.getTimestamp());
        response.put("previousHash", newBlock.getPreviousHash());
        response.put("nonce", newBlock.getNonce());
        
        log.info("Bloque #{} minado exitosamente con hash: {}", 
                 newBlock.getIndex(), newBlock.getHash());
        
        return response;
    }
    
    /**
     * Obtiene información general de la blockchain.
     * 
     * @return información resumida de la blockchain
     */
    public BlockchainInfo getBlockchainInfo() {
        return blockchain.getInfo();
    }
    
    /**
     * Valida la integridad de la blockchain.
     * 
     * @return true si la blockchain es válida
     */
    public boolean validateBlockchain() {
        boolean isValid = blockchain.isChainValid();
        if (!isValid) {
            log.error("¡La blockchain ha sido comprometida!");
        }
        return isValid;
    }
    
    /**
     * Obtiene todos los bloques de la cadena.
     * 
     * @return lista de bloques con su información
     */
    public List<Map<String, Object>> getAllBlocks() {
        List<Map<String, Object>> blocks = new ArrayList<>();
        
        for (Block block : blockchain.getChain()) {
            Map<String, Object> blockInfo = new HashMap<>();
            blockInfo.put("index", block.getIndex());
            blockInfo.put("timestamp", block.getTimestamp());
            blockInfo.put("data", block.getData());
            blockInfo.put("hash", block.getHash());
            blockInfo.put("previousHash", block.getPreviousHash());
            blockInfo.put("nonce", block.getNonce());
            blocks.add(blockInfo);
        }
        
        return blocks;
    }
    
    /**
     * Obtiene un bloque específico por su índice.
     * 
     * @param index índice del bloque
     * @return información del bloque o null si no existe
     */
    public Map<String, Object> getBlock(int index) {
        if (index < 0 || index >= blockchain.getChain().size()) {
            return null;
        }
        
        Block block = blockchain.getChain().get(index);
        Map<String, Object> blockInfo = new HashMap<>();
        blockInfo.put("index", block.getIndex());
        blockInfo.put("timestamp", block.getTimestamp());
        blockInfo.put("data", block.getData());
        blockInfo.put("hash", block.getHash());
        blockInfo.put("previousHash", block.getPreviousHash());
        blockInfo.put("nonce", block.getNonce());
        
        return blockInfo;
    }
}