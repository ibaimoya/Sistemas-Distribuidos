package es.ubu.lsi.web.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import es.ubu.lsi.web.entity.Usuario;

@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;

    @Test
    void testFindByNombre() {
        // Comprueba findByNombre encuentra usuario existente.
        Usuario usuario = new Usuario("nombre1", "email1", "hash1");
        repository.save(usuario);

        Optional<Usuario> found = repository.findByNombre("nombre1");
        assertTrue(found.isPresent());
        assertEquals("email1", found.get().getEmail());
    }

    @Test
    void testFindByEmail() {
        // Comprueba findByEmail encuentra usuario existente.
        Usuario usuario = new Usuario("nombre2", "email2", "hash2");
        repository.save(usuario);

        Optional<Usuario> found = repository.findByEmail("email2");
        assertTrue(found.isPresent());
        assertEquals("nombre2", found.get().getNombre());
    }

    @Test
    void testNotFound() {
        // Comprueba findByNombre y findByEmail devuelven vacío si no existe.
        Optional<Usuario> byName = repository.findByNombre("noexist");
        Optional<Usuario> byEmail = repository.findByEmail("noexist");
        assertFalse(byName.isPresent());
        assertFalse(byEmail.isPresent());
    }
}