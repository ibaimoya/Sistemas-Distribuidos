package es.ubu.lsi.web.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import es.ubu.lsi.web.entity.Role;
import es.ubu.lsi.web.entity.Usuario;
import es.ubu.lsi.web.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

/**
 * Clase que carga datos iniciales en la base de datos al arrancar la aplicación.
 * Crea un usuario administrador por defecto si no existe.
 * 
 * @author Ibai Moya Aroz
 * @version 1.0
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder  encoder;

    /**
     * Carga datos iniciales en la base de datos al arrancar la aplicación.
     * Crea un usuario administrador por defecto si no existe.
     *
     * @param args los argumentos de la aplicación
     */
    @Override
    public void run(ApplicationArguments args) {

        String adminEmail = "admin@admin.com";
        String username = "admin";
        String password = username;

        Usuario admin = usuarioRepo.findByNombre(username).orElseGet(() -> {
            Usuario newAdmin = new Usuario(
                username,
                adminEmail,
                encoder.encode(password)
            );
            newAdmin.setRole(Role.ADMIN);
            return usuarioRepo.save(newAdmin);
        });

        /* Log para confirmar la creación del usuario administrador. */
        System.out.println("Usuario administrador creado: " + admin.getNombre());
        System.out.println("Email: " + admin.getEmail());
        System.out.println("Contraseña: " + password);
    }
}