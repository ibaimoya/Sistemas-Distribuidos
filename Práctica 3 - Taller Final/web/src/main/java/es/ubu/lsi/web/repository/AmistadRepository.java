package es.ubu.lsi.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.ubu.lsi.web.entity.Amistad;
import es.ubu.lsi.web.entity.Usuario;

/**
 * Repositorio para gestionar las operaciones de amistad en la base de datos.
 * 
 * @author Ibai Moya Aroz
 * 
 * @version 1.0
 * @since 1.0
 */
public interface AmistadRepository extends JpaRepository<Amistad, Long> {
    
    /**
     * Encuentra todas las amistades de un usuario.
     * 
     * @param usuario el usuario cuyas amistades se desean obtener
     * @return una lista de amistades del usuario
     */
    List<Amistad> findByUsuario(Usuario usuario);
    
    /**
     * Busca una amistad específica entre dos usuarios.
     * 
     * @param usuario el usuario principal
     * @param amigo el amigo
     * @return la amistad si existe
     */
    Optional<Amistad> findByUsuarioAndAmigo(Usuario usuario, Usuario amigo);
    
    /**
     * Verifica si dos usuarios son amigos (en cualquier dirección).
     * 
     * @param usuario1 primer usuario
     * @param usuario2 segundo usuario
     * @return true si son amigos
     */
    @Query("SELECT COUNT(a) > 0 FROM Amistad a WHERE " +
           "(a.usuario = :usuario1 AND a.amigo = :usuario2) OR " +
           "(a.usuario = :usuario2 AND a.amigo = :usuario1)")
    boolean sonAmigos(@Param("usuario1") Usuario usuario1, @Param("usuario2") Usuario usuario2);
    
    /**
     * Elimina todas las amistades entre dos usuarios (ambas direcciones).
     * 
     * @param usuario1 primer usuario
     * @param usuario2 segundo usuario
     */
    void deleteByUsuarioAndAmigo(Usuario usuario1, Usuario usuario2);
    
    /**
     * Cuenta el número de amigos de un usuario.
     * 
     * @param usuario el usuario
     * @return el número de amigos
     */
    long countByUsuario(Usuario usuario);
}