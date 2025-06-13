package es.ubu.lsi.web.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase que representa una valoración de una película por un usuario.
 * Una valoración incluye el ID del usuario, el ID de la película y la puntuación dada.
 * También incluye información del bloque de blockchain donde se ha registrado.
 * 
 * @author Ibai Moya Aroz
 * 
 * @version 1.0
 * @since 1.0
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Valoracion {

    /**
     * ID único de la valoración.
     * Este ID es generado automáticamente por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario que realiza la valoración.
     * Relación ManyToOne con la entidad Usuario.
     */
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /**
     * ID de la película valorada.
     * Este ID corresponde al ID de la película en la base de datos de películas.
     */
    @Column(nullable = false)
    private Integer movieId;

    /**
     * Puntuación dada a la película.
     * Este campo es obligatorio y debe estar entre 1 y 5.
     */
    @Column(nullable = false)
    private Integer rating;
    
    /**
     * Hash del bloque de blockchain donde se ha registrado esta valoración.
     * Permite verificar la integridad de la valoración.
     */
    @Column(length = 64)
    private String blockHash;
    
    /**
     * Índice del bloque en la blockchain.
     */
    @Column
    private Integer blockIndex;

    /**
     * Constructor para crear una nueva valoración.
     * Este constructor deja que el ID sea generado automáticamente por la base de datos.
     *
     * @param usuario  El usuario que realiza la valoración.
     * @param movieId  El ID de la película valorada.
     * @param rating   La puntuación dada a la película (1-5).
     */
    public Valoracion(Usuario usuario, Integer movieId, Integer rating) {
        this.usuario = usuario;
        this.movieId = movieId;
        this.rating  = rating;
    }
}