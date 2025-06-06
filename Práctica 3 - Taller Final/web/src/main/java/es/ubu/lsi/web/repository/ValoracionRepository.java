package es.ubu.lsi.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.ubu.lsi.web.entity.Usuario;
import es.ubu.lsi.web.entity.Valoracion;

/**
 * Repositorio para gestionar las operaciones de valoraciones en la base de datos.
 * Proporciona métodos para buscar, listar y contar valoraciones de películas por usuario.
 * 
 * @author Ibai Moya Aroz
 * @version 1.0
 * @since 1.0
 */
public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {

    /**
     * Encuentra una valoración específica de un usuario por su ID de película.
     * 
     * @param usuario el usuario que realizó la valoración
     * @param movieId el ID de la película valorada
     * @return un objeto Valoracion si se encuentra, o vacío si no existe
     */
    Optional<Valoracion> findByUsuarioAndMovieId(Usuario usuario, Integer movieId);

    /**
     * Encuentra todas las valoraciones de un usuario ordenadas por ID de forma descendente.
     * 
     * @param usuario el usuario cuyas valoraciones se desean obtener
     * @return una lista de valoraciones del usuario ordenadas por ID descendente
     */
    List<Valoracion> findByMovieId(Integer movieId);

    /**
     * Cuenta el número de valoraciones de una película específica.
     * 
     * @param movieId el ID de la película cuyas valoraciones se desean contar
     * @return el número de valoraciones para la película especificada
     */
    long countByMovieId(Integer movieId);
}