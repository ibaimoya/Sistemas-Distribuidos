package es.ubu.lsi.web.blockchain;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Implementación de una blockchain para almacenar valoraciones de películas.
 * Cada valoración se registra como un bloque inmutable en la cadena.
 * 
 * @author Ibai Moya Aroz
 * 
 * @version 1.0
 * @since 1.0
 */
@Getter
public class Blockchain {
    
    /** Lista de bloques en la cadena. */
    private List<Block> chain;
    
    /** Dificultad para minar bloques (número de ceros al principio del hash). */
    private int difficulty;
    
    /**
     * Constructor de la blockchain.
     * Crea el bloque génesis automáticamente.
     */
    public Blockchain() {
        this.chain = new ArrayList<>();
        this.difficulty = 3; /* Dificultad baja para demostración. */

        /* Crear bloque génesis. */
        addBlock(createGenesisBlock());
    }
    
    /**
     * Crea el primer bloque de la cadena (génesis).
     * 
     * @return el bloque génesis
     */
    private Block createGenesisBlock() {
        return new Block(0, "Genesis Block", "0");
    }
    
    /**
     * Obtiene el último bloque de la cadena.
     * 
     * @return el último bloque
     */
    public Block getLatestBlock() {
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
        
        Block previousBlock = getLatestBlock();
        Block newBlock = new Block(
            previousBlock.getIndex() + 1,
            data,
            previousBlock.getHash()
        );
        
        addBlock(newBlock);
        return newBlock;
    }
    
    /**
     * Valida la integridad de toda la blockchain.
     * 
     * @return true si la cadena es válida, false si hay algún problema
     */
    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);
            
            /* Verificar que el hash del bloque actual es correcto. */
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                return false;
            }
            
            /* Verificar que el hash del bloque anterior coincide. */
            if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                return false;
            }
            
            /* Verificar que el hash cumple con la dificultad. */   
            String target = new String(new char[difficulty]).replace('\0', '0');
            if (!currentBlock.getHash().substring(0, difficulty).equals(target)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Obtiene información resumida de la blockchain.
     * 
     * @return mapa con estadísticas de la cadena
     */
    public BlockchainInfo getInfo() {
        return BlockchainInfo.builder()
            .totalBlocks(chain.size())
            .difficulty(difficulty)
            .isValid(isChainValid())
            .latestBlockHash(getLatestBlock().getHash())
            .build();
    }
}