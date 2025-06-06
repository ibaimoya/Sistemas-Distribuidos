package es.ubu.lsi.web.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import es.ubu.lsi.web.entity.Role;
import es.ubu.lsi.web.entity.Usuario;
import es.ubu.lsi.web.entity.Valoracion;
import es.ubu.lsi.web.repository.FavoritoRepository;
import es.ubu.lsi.web.repository.UsuarioRepository;
import es.ubu.lsi.web.repository.ValoracionRepository;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de usuarios y favoritos en el panel de administración.
 * Permite listar usuarios, obtener favoritos de un usuario y eliminar usuarios.
 * Requiere rol de administrador para acceder a sus endpoints.
 * 
 * @author Ibai Moya Aroz
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    /** Repositorios para acceder a los datos de usuarios y favoritos. */
    private final UsuarioRepository usuarioRepo;

    /** Repositorio para acceder a los datos de favoritos de los usuarios. */
    private final FavoritoRepository favoritoRepo;

    /** Repositorio para acceder a las valoraciones de los usuarios. */
    private final ValoracionRepository valoracionRepository;

    /**
     * Lista todos los usuarios registrados en la aplicación, excluyendo al administrador.
     * 
     * @return una lista de mapas con los datos de los usuarios (id, nombre, email)
     */
    @GetMapping("/users")
    public List<Map<String, Object>> listUsers() {
        return usuarioRepo.findAll().stream()
            .filter(u -> u.getRole() != Role.ADMIN)
            .map(u -> Map.<String,Object>of(
                    "id",    u.getId(),
                    "nombre",u.getNombre(),
                    "email", u.getEmail()))
            .toList();
    }

    /**
     * Obtiene los favoritos de un usuario por su ID.
     * 
     * @param id el ID del usuario
     * @return una lista de mapas con los datos de los favoritos (movieId, title, poster)
     */
    @GetMapping("/users/{id}/favorites")
    public List<Map<String, Object>> userFavorites(@PathVariable Long id) {
        Usuario u = usuarioRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return favoritoRepo.findByUsuarioOrderByIdDesc(u).stream()
            .map(f -> Map.<String,Object>of(
                    "movieId", f.getMovieId(),
                    "title",   f.getTitle(),
                    "poster",  f.getPosterPath()))
            .toList();
    }

    /**
     * Elimina un usuario por su ID y sus favoritos asociados.
     * 
     * @param id el ID del usuario a eliminar
     * @return una respuesta vacía con código 204 No Content si se elimina correctamente,
     *         o 404 Not Found si el usuario no existe
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!usuarioRepo.existsById(id))
            return ResponseEntity.notFound().build();

        favoritoRepo.deleteAll(favoritoRepo.findByUsuarioOrderByIdDesc(
                usuarioRepo.getReferenceById(id)));
        usuarioRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene las películas favoritas de un usuario por su ID.
     * 
     * @param id el ID del usuario
     * @return una lista de mapas con los datos de las películas favoritas (id, title, poster_path, rating)
     */
    @GetMapping("/users/{id}/movies")
    public List<Map<String, Object>> userMovies(@PathVariable Long id) {

        Usuario u = usuarioRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return favoritoRepo.findByUsuarioOrderByIdDesc(u).stream()
            .map(fav -> {
                Integer rating = valoracionRepository
                        .findByUsuarioAndMovieId(u, fav.getMovieId())
                        .map(Valoracion::getRating)
                        .orElse(null);

                return Map.<String,Object>of(
                        "id",          fav.getMovieId(),
                        "title",       fav.getTitle(),
                        "poster_path", fav.getPosterPath(),
                        "rating",      rating == null ? "-" : rating 
                        );
            })
            .toList();
    }
}