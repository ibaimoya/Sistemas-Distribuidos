package es.ubu.lsi.web.blockchain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que contiene información resumida sobre el estado de la blockchain.
 * 
 * @author Ibai Moya Aroz
 * 
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockchainInfo {
    
    /** Número total de bloques en la cadena. */
    private int totalBlocks;
    
    /** Dificultad actual de minado. */
    private int difficulty;
    
    /** Indica si la cadena es válida. */
    private boolean isValid;
    
    /** Hash del último bloque. */
    private String latestBlockHash;
}