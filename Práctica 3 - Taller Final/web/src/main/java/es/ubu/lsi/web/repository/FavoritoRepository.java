package es.ubu.lsi.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import es.ubu.lsi.web.entity.Favorito;
import es.ubu.lsi.web.entity.Usuario;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    
    List<Favorito> findByUsuarioOrderByIdDesc(Usuario usuario);
    
    Optional<Favorito> findByUsuarioAndMovieId(Usuario usuario, Integer movieId);
    
    @Transactional
    void deleteByUsuarioAndMovieId(Usuario usuario, Integer movieId);
}