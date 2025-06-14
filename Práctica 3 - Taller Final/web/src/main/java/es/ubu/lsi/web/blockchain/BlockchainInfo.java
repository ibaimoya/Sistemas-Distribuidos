package es.ubu.lsi.web.blockchain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que contiene información resumida sobre el estado de la blockchain.
 * 
 * @author Ibai Moya Aroz
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockchainInfo {
    
    /** Número total de bloques en la cadena */
    private int totalBlocks;
    
    /** Dificultad actual de minado */
    private int difficulty;
    
    /** Indica si la cadena es válida */
    @JsonProperty("isValid")
    private boolean valid;
    
    /** Hash del último bloque */
    private String latestBlockHash;
    
    /**
     * Getter personalizado para mantener compatibilidad.
     * Jackson usará este método para serializar el campo como "isValid".
     * 
     * @return true si la blockchain es válida
     */
    @JsonProperty("isValid")
    public boolean isValid() {
        return valid;
    }
}