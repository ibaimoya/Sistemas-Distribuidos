package es.ubu.lsi.web.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa un favorito de un usuario.
 * Un favorito es una película que un usuario ha marcado como favorita.
 * 
 * @author Ibai Moya Aroz
 * 
 * @version 1.0
 * @since 1.0
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favorito {

    /**
     * ID único del favorito.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario que ha marcado la película como favorita.
     * Relación ManyToOne con la entidad Usuario.
     */
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /**
     * ID de la película favorita.
     * Este ID corresponde al ID de la película en la base de datos de películas.
     */
    @Column(nullable = false)
    private Integer movieId;

    /**
     * Título de la película favorita.
     * Este campo es obligatorio y no puede ser nulo.
     */
    @Column(nullable = false)
    private String title;

    /**
     * Ruta del póster de la película favorita.
     * Este campo puede ser nulo si no se proporciona una imagen.
     */
    @Column
    private String posterPath;

    /**
     * Breve descripción de la película favorita.
     * Este campo puede contener una descripción detallada de la película.
     */
    @Column(length = 1000)
    private String overview;

    /**
     * Constructor para crear un favorito a partir de un usuario y los datos de una película.
     * Deja que la ID la genere la base de datos automáticamente.
     *
     * @param usuario el usuario que guarda el favorito
     * @param movieId el ID de la película
     * @param title el título de la película
     * @param posterPath la ruta del póster de la película
     * @param overview una breve descripción de la película
     */
    public Favorito(Usuario usuario, Integer movieId, String title, String posterPath, String overview) {
        this.usuario = usuario;
        this.movieId = movieId;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
    }
}