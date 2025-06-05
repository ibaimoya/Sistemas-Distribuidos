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
        usuarioRepo.findByNombre("admin").orElseGet(() -> {
            Usuario admin = new Usuario(
                "admin",
                "admin@admin.com",
                encoder.encode("admin")
            );
            admin.setRole(Role.ADMIN);
            return usuarioRepo.save(admin);
        });
    }
}