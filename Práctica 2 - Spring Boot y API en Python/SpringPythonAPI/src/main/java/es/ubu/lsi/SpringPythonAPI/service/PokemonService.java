package es.ubu.lsi.SpringPythonAPI.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import es.ubu.lsi.SpringPythonAPI.entity.Pokemon;
import es.ubu.lsi.SpringPythonAPI.entity.Usuario;
import es.ubu.lsi.SpringPythonAPI.repository.PokemonRepository;
import es.ubu.lsi.SpringPythonAPI.repository.UsuarioRepository;

@Service
public class PokemonService {

    private final PokemonRepository repo;
    private final UsuarioRepository usuarioRepo;

    public PokemonService(PokemonRepository repo, UsuarioRepository usuarioRepo) {
        this.repo = repo;
        this.usuarioRepo = usuarioRepo;
    }

    public void guardar(String nombreUsuario,
        String nombrePoke,
        Integer numero,
        String imagenUrl) {

        Usuario u = usuarioRepo.findByNombre(nombreUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no existe"));

        Pokemon pokemon = new Pokemon(nombrePoke, numero, imagenUrl, u);
        repo.save(pokemon);
    }

    public List<Pokemon> listar(String usuarioNombre) {
        return repo.findByUsuario_Nombre(usuarioNombre);
    }

    @Transactional
    public boolean borrar(Long id, String usuario){
        // Solo deja borrar si pertenece al usuario
        return repo.findByIdAndUsuario_Nombre(id, usuario)
            .map(pokemon -> {
                repo.delete(pokemon);
                return true;
            })
            .orElse(false);
    }
}