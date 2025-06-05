package es.ubu.lsi.web.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import es.ubu.lsi.web.entity.Usuario;
import es.ubu.lsi.web.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

/**
 * Servicio personalizado para cargar los detalles del usuario.
 * Implementa UserDetailsService para proporcionar la funcionalidad de autenticación.
 * 
 * @author Ibai Moya Aroz
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * Repositorio de usuarios para acceder a la base de datos.
     */
    private final UsuarioRepository repo;

    /**
     * Carga un usuario por su nombre de usuario.
     * 
     * @param username el nombre de usuario
     * @return los detalles del usuario
     * @throws UsernameNotFoundException si el usuario no se encuentra
     */
    @Override
    public UserDetails loadUserByUsername(String username)
                              throws UsernameNotFoundException {

        Usuario u = repo.findByNombre(username)
                        .orElseThrow(() ->
                             new UsernameNotFoundException("Usuario no encontrado"));

        return User.withUsername(u.getNombre())
                   .password(u.getPasswordHash())
                   .roles(u.getRole().name())
                   .build();
    }
}