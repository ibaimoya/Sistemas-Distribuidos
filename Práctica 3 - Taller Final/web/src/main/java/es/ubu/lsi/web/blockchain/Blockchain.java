package es.ubu.lsi.web.blockchain;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación de una blockchain para almacenar valoraciones de películas.
 * Cada valoración se registra como un bloque inmutable en la cadena.
 * 
 * @author Ibai Moya Aroz
 * @version 1.0
 * @since 1.0
 */
@Getter
@Slf4j
public class Blockchain {
    
    /** Lista de bloques en la cadena */
    private final List<Block> chain;
    
    /** Dificultad para minar bloques (número de ceros al principio del hash) */
    private final int difficulty;
    
    /**
     * Constructor de la blockchain.
     * Crea el bloque génesis automáticamente.
     */
    public Blockchain() {
        this.chain = new ArrayList<>();
        this.difficulty = 3; // Dificultad baja para demostración
        
        /* Crea y mina el bloque génesis. */
        Block genesis = createGenesisBlock();
        genesis.mineBlock(difficulty);
        chain.add(genesis);
        
        log.info("Bloque génesis creado con hash: {}", genesis.getHash());
    }
    
    /**
     * Crea el primer bloque de la cadena (génesis).
     * 
     * @return el bloque génesis
     */
    private Block createGenesisBlock() {
        return new Block(0, "Genesis Block", "0000000000000000");
    }
    
    /**
     * Obtiene el último bloque de la cadena.
     * 
     * @return el último bloque
     */
    public Block getLatestBlock() {
        if (chain.isEmpty()) {
            throw new IllegalStateException("La blockchain está vacía");
        }
        return chain.get(chain.size() - 1);
    }
    
    /**
     * Añade un nuevo bloque a la cadena.
     * 
     * @param block el bloque a añadir
     */
    private void addBlock(Block block) {
        block.mineBlock(difficulty);
        chain.add(block);
    }
    
    /**
     * Crea y añade un nuevo bloque con datos de valoración.
     * 
     * @param userId ID del usuario
     * @param movieId ID de la película
     * @param rating valoración (1-5)
     * @return el bloque creado
     */
    public Block addRating(Long userId, Integer movieId, Integer rating) {
        String data = String.format("USER:%d|MOVIE:%d|RATING:%d|TIME:%d", 
                                    userId, movieId, rating, System.currentTimeMillis());

        /* Si no hay bloques, crea el génesis primero. */
        if (chain.isEmpty()) {
            log.warn("Blockchain vacía, creando bloque génesis");
            Block genesis = createGenesisBlock();
            genesis.mineBlock(difficulty);
            chain.add(genesis);
        }
        
        Block previousBlock = getLatestBlock();
        Block newBlock = new Block(
            previousBlock.getIndex() + 1,
            data,
            previousBlock.getHash()
        );
        
        addBlock(newBlock);
        log.info("Nuevo bloque añadido: índice={}, hash={}", newBlock.getIndex(), newBlock.getHash());
        return newBlock;
    }
    
    /**
     * Valida la integridad de toda la blockchain.
     * 
     * @return true si la cadena es válida, false si hay algún problema
     */
    public boolean isChainValid() {

        /* Si no hay bloques, la cadena es técnicamente válida pero vacía. */
        if (chain.isEmpty()) {
            log.warn("Blockchain vacía");
            return true;
        }

        /* Si solo hay un bloque (génesis), verifica solo ese. */
        if (chain.size() == 1) {
            Block genesis = chain.get(0);
            String calculatedHash = genesis.calculateHash();
            if (!genesis.getHash().equals(calculatedHash)) {
                log.error("Hash del bloque génesis no coincide");
                return false;
            }

            /* Verifica que cumple con la dificultad. */
            String target = new String(new char[difficulty]).replace('\0', '0');
            int hashLength = genesis.getHash().length();
            int checkLength = Math.min(difficulty, hashLength);
            String hashPrefix = genesis.getHash().substring(0, checkLength);
            if (!hashPrefix.equals(target.substring(0, checkLength))) {
                log.error("Hash del génesis no cumple con la dificultad. Hash: {}, Prefijo: {}", 
                         genesis.getHash(), hashPrefix);
                return false;
            }
            
            return true;
        }

        /* Verifica el resto de la cadena. */
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            /* Verifica que el hash del bloque actual es correcto. */
            String calculatedHash = currentBlock.calculateHash();
            if (!currentBlock.getHash().equals(calculatedHash)) {
                log.error("Hash del bloque {} no coincide. Esperado: {}, Calculado: {}", 
                         i, currentBlock.getHash(), calculatedHash);
                return false;
            }

            /* Verifica que el hash del bloque anterior coincide. */
            if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                log.error("Hash previo del bloque {} no coincide con el hash del bloque {}", 
                         i, i-1);
                return false;
            }

            /* Verifica que el hash cumple con la dificultad. */
            String target = new String(new char[difficulty]).replace('\0', '0');
            int hashLength = currentBlock.getHash().length();
            int checkLength = Math.min(difficulty, hashLength);
            String hashPrefix = currentBlock.getHash().substring(0, checkLength);
            if (!hashPrefix.equals(target.substring(0, checkLength))) {
                log.error("Hash del bloque {} no cumple con la dificultad {}. Prefijo: {}", 
                         i, difficulty, hashPrefix);
                return false;
            }
        }
        
        log.info("Blockchain válida con {} bloques", chain.size());
        return true;
    }
    
    /**
     * Obtiene información resumida de la blockchain.
     * 
     * @return información de la cadena
     */
    public BlockchainInfo getInfo() {
        String latestHash = "N/A";
        
        try {
            if (!chain.isEmpty()) {
                latestHash = getLatestBlock().getHash();
            }
        } catch (Exception e) {
            log.error("Error obteniendo el último bloque: ", e);
        }
        
        return BlockchainInfo.builder()
            .totalBlocks(chain.size())
            .difficulty(difficulty)
            .valid(isChainValid())
            .latestBlockHash(latestHash)
            .build();
    }
}