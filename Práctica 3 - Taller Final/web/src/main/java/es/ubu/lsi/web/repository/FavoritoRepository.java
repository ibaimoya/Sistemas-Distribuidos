package es.ubu.lsi.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import es.ubu.lsi.web.entity.Favorito;
import es.ubu.lsi.web.entity.Usuario;

/**
 * Repositorio para gestionar las operaciones de favoritos en la base de datos.
 * Proporciona métodos para buscar, eliminar y listar favoritos de un usuario.
 * 
 * @author Ibai Moya Aroz
 * @version 1.0
 * @since 1.0
 */
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    
    /**
     * Encuentra todos los favoritos de un usuario ordenados por ID de forma descendente.
     * 
     * @param usuario el usuario cuyos favoritos se desean obtener
     * @return una lista de favoritos del usuario ordenados por ID descendente
     */
    List<Favorito> findByUsuarioOrderByIdDesc(Usuario usuario);
    
    /**
     * Busca un favorito específico de un usuario por su ID de película.
     * 
     * @param usuario el usuario que tiene el favorito
     * @param movieId el ID de la película del favorito
     * @return un objeto Favorito si se encuentra, o vacío si no existe
     */
    Optional<Favorito> findByUsuarioAndMovieId(Usuario usuario, Integer movieId);
    
    /**
     * Elimina un favorito específico de un usuario por su ID de película.
     * 
     * @param usuario el usuario que tiene el favorito
     * @param movieId el ID de la película del favorito a eliminar
     */
    @Transactional
    void deleteByUsuarioAndMovieId(Usuario usuario, Integer movieId);
}