package es.ubu.lsi.SpringPythonAPI.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import es.ubu.lsi.SpringPythonAPI.entity.Pokemon;

/**
 * Accede a la tabla Pokemon mediante Spring Data JPA.
 * 
 * @author Ibai Moya Aroz
 *
 * @version 1.0
 * @since 1.0 
 */
public interface PokemonRepository extends JpaRepository<Pokemon, Long> {
    boolean existsByUsuario_NombreAndNombre(String usuario, String nombre);
    List<Pokemon> findByUsuario_Nombre(String usuario);
}