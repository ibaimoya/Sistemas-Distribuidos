package es.ubu.lsi.web.repository;

import es.ubu.lsi.web.entity.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Encontrar usuario por email existente")
    void testFindByEmail() {
        // Prepara un usuario de prueba y lo guarda en el repositorio.
        Usuario usuario = new Usuario();
        usuario.setNombre("Juan");
        usuario.setEmail("juan@example.com");
        usuarioRepository.save(usuario);

        // Ejecuta la búsqueda por email.
        Optional<Usuario> encontrado = usuarioRepository.findByEmail("juan@example.com");

        // Verifica que el usuario fue encontrado y el nombre coincide.
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("Juan");
    }

    @Test
    @DisplayName("Encontrar usuario por nombre existente")
    void testFindByNombre() {
        // Prepara un usuario de prueba y lo guarda en el repositorio.
        Usuario usuario = new Usuario();
        usuario.setNombre("María");
        usuario.setEmail("maria@example.com");
        usuarioRepository.save(usuario);

        // Ejecuta la búsqueda por nombre.
        Optional<Usuario> encontrado = usuarioRepository.findByNombre("María");

        // Verifica que el usuario fue encontrado y el email coincide.
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getEmail()).isEqualTo("maria@example.com");
    }

    @Test
    @DisplayName("Retornar vacío al buscar usuario inexistente")
    void testNotFound() {
        // Ejecuta la búsqueda con datos que no existen.
        Optional<Usuario> encontradoEmail = usuarioRepository.findByEmail("no-existe@example.com");
        Optional<Usuario> encontradoNombre = usuarioRepository.findByNombre("NoExiste");

        // Verifica que no se encontró ningún usuario.
        assertThat(encontradoEmail).isNotPresent();
        assertThat(encontradoNombre).isNotPresent();
    }
}
