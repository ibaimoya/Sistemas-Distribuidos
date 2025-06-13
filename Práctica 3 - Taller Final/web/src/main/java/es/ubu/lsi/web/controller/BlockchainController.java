package es.ubu.lsi.web.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.ubu.lsi.web.blockchain.BlockchainInfo;
import es.ubu.lsi.web.service.BlockchainService;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para consultar información de la blockchain.
 * Permite ver el estado de la cadena y explorar los bloques.
 * 
 * @author Ibai Moya Aroz
 * 
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/blockchain")
@RequiredArgsConstructor
public class BlockchainController {
    
    /** Servicio de blockchain */
    private final BlockchainService blockchainService;
    
    /**
     * Obtiene información general de la blockchain.
     * 
     * @return información resumida de la blockchain
     */
    @GetMapping("/info")
    public ResponseEntity<BlockchainInfo> getBlockchainInfo() {
        return ResponseEntity.ok(blockchainService.getBlockchainInfo());
    }
    
    /**
     * Obtiene todos los bloques de la blockchain.
     * 
     * @return lista de todos los bloques
     */
    @GetMapping("/blocks")
    public ResponseEntity<List<Map<String, Object>>> getAllBlocks() {
        return ResponseEntity.ok(blockchainService.getAllBlocks());
    }
    
    /**
     * Obtiene un bloque específico por su índice.
     * 
     * @param index índice del bloque
     * @return información del bloque o 404 si no existe
     */
    @GetMapping("/blocks/{index}")
    public ResponseEntity<Map<String, Object>> getBlock(@PathVariable int index) {
        Map<String, Object> block = blockchainService.getBlock(index);
        if (block == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(block);
    }
    
    /**
     * Valida la integridad de toda la blockchain.
     * 
     * @return resultado de la validación
     */
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateBlockchain() {
        boolean isValid = blockchainService.validateBlockchain();
        return ResponseEntity.ok(Map.of(
            "valid", isValid,
            "message", isValid ? "La blockchain es válida" : "¡La blockchain ha sido comprometida!"
        ));
    }
}