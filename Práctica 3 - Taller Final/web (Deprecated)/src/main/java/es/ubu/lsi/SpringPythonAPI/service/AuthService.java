package es.ubu.lsi.SpringPythonAPI.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.ubu.lsi.SpringPythonAPI.entity.Usuario;
import es.ubu.lsi.SpringPythonAPI.repository.UsuarioRepository;

/**
 * Servicio de autenticación y registro de usuarios.
 * Proporciona métodos para iniciar sesión, registrar nuevos usuarios
 * y verificar la existencia de usuarios en la base de datos.
 *
 * @author Ibai Moya Aroz
 *
 * @version 1.0
 * @since 1.0
 */
@Service
public class AuthService {

    private final UsuarioRepository repo;
    private final PasswordEncoder   encoder;

    public AuthService(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    /**
     * Comprueba si el usuario existe y si la contraseña es correcta.
     * Utiliza el codificador de contraseñas para comparar la contraseña
     * 
     * @param nombre Nombre de usuario
     * @param passwordPlano Contraseña en texto plano
     * @return true si el usuario existe y la contraseña es correcta, false en caso contrario
     */
    public boolean login(String nombre, String passwordPlano) {
        return repo.findByNombre(nombre)
                   .map(u -> encoder.matches(passwordPlano, u.getPasswordHash()))
                   .orElse(false);
    }

    /**
     * Comprueba si ya existe un usuario o email en la base de datos.
     * 
     * @param nombre Nombre de usuario
     * @param email Email del usuario
     * @return true si el usuario o email ya existen, false en caso contrario
     */
    public boolean existeUsuario(String nombre, String email) {
        return repo.findByNombre(nombre).isPresent() || repo.findByEmail(email).isPresent();
    }

    /**
     * Registra un nuevo usuario en la base de datos.
     * Utiliza el codificador de contraseñas para hashear la contraseña
     * 
     * @param nombre Nombre de usuario
     * @param email Email del usuario
     * @param passwordPlano Contraseña en texto plano
     */
    public void registrar(String nombre, String email, String passwordPlano) {
        String hash = encoder.encode(passwordPlano);
        Usuario nuevo = new Usuario(nombre, email, hash);
        repo.save(nuevo);
    }
}