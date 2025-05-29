package es.ubu.lsi.web.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.ubu.lsi.web.entity.Usuario;

/**
 * Accede a la tabla usuario mediante Spring Data JPA.
 * 
 * @author Ibai Moya Aroz
 *
 * @version 1.0
 * @since 1.0 
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su nombre.
     * 
     * @param nombre Nombre del usuario
     * @return Usuario encontrado o vacío si no existe
     */
    Optional<Usuario> findByNombre(String nombre);

    /**
     * Busca un usuario por su email.
     * 
     * @param email Email del usuario
     * @return Usuario encontrado o vacío si no existe
     */
    Optional<Usuario> findByEmail(String email);
}