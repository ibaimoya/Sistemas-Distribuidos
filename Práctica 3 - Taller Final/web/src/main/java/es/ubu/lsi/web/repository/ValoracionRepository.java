package es.ubu.lsi.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.ubu.lsi.web.entity.Usuario;
import es.ubu.lsi.web.entity.Valoracion;

public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {

    Optional<Valoracion> findByUsuarioAndMovieId(Usuario usuario, Integer movieId);

    List<Valoracion> findByMovieId(Integer movieId);

    long countByMovieId(Integer movieId);
}