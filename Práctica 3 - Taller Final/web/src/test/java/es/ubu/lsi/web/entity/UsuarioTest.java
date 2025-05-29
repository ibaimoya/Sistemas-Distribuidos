package es.ubu.lsi.web.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UsuarioTest {

    @Test
    void testDefaultConstructorAndSettersGetters() {
        // Comprueba getters y setters con constructor por defecto.
        Usuario usuario = new Usuario();
        usuario.setNombre("nombre");
        usuario.setEmail("email");
        usuario.setPasswordHash("hash");

        assertNull(usuario.getId());
        assertEquals("nombre", usuario.getNombre());
        assertEquals("email", usuario.getEmail());
        assertEquals("hash", usuario.getPasswordHash());
    }

    @Test
    void testFullConstructor() {
        // Comprueba getters con constructor completo.
        Usuario usuario = new Usuario("nombre", "email", "hash");

        assertNull(usuario.getId());
        assertEquals("nombre", usuario.getNombre());
        assertEquals("email", usuario.getEmail());
        assertEquals("hash", usuario.getPasswordHash());
    }
}