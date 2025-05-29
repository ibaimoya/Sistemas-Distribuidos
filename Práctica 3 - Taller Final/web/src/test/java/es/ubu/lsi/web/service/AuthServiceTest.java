package es.ubu.lsi.web.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import es.ubu.lsi.web.entity.Usuario;
import es.ubu.lsi.web.repository.UsuarioRepository;

class AuthServiceTest {

    private UsuarioRepository repo;
    private PasswordEncoder encoder;
    private AuthService auth;

    @BeforeEach
    void setUp() {
        // Prepara mocks de repositorio y codificador de contraseñas.
        repo = mock(UsuarioRepository.class);
        encoder = mock(PasswordEncoder.class);
        auth = new AuthService(repo, encoder);
    }

    @Test
    void testLoginSuccess() {
        // Comprueba que login devuelve verdadero si las credenciales son válidas.
        String nombre = "user";
        String rawPassword = "pass";
        String hash = "hashed";
        Usuario user = new Usuario(nombre, "email", hash);

        when(repo.findByNombre(nombre)).thenReturn(Optional.of(user));
        when(encoder.matches(rawPassword, hash)).thenReturn(true);

        assertTrue(auth.login(nombre, rawPassword));
        verify(encoder).matches(rawPassword, hash);
    }

    @Test
    void testLoginWrongPassword() {
        // Comprueba que login devuelve falso si la contraseña es incorrecta.
        String nombre = "user";
        String rawPassword = "wrong";
        String hash = "hashed";
        Usuario user = new Usuario(nombre, "email", hash);

        when(repo.findByNombre(nombre)).thenReturn(Optional.of(user));
        when(encoder.matches(rawPassword, hash)).thenReturn(false);

        assertFalse(auth.login(nombre, rawPassword));
        verify(encoder).matches(rawPassword, hash);
    }

    @Test
    void testLoginUserNotFound() {
        // Comprueba que login devuelve falso si el usuario no existe.
        String nombre = "unknown";

        when(repo.findByNombre(nombre)).thenReturn(Optional.empty());

        assertFalse(auth.login(nombre, "any"));
        verify(encoder, never()).matches(any(), any());
    }

    @Test
    void testExisteUsuarioByName() {
        // Comprueba que existeUsuario devuelve verdadero si el nombre existe.
        String nombre = "user";
        String email = "email";

        when(repo.findByNombre(nombre)).thenReturn(Optional.of(new Usuario()));
        when(repo.findByEmail(email)).thenReturn(Optional.empty());

        assertTrue(auth.existeUsuario(nombre, email));
    }

    @Test
    void testExisteUsuarioByEmail() {
        // Comprueba que existeUsuario devuelve verdadero si el email existe.
        String nombre = "user";
        String email = "email";

        when(repo.findByNombre(nombre)).thenReturn(Optional.empty());
        when(repo.findByEmail(email)).thenReturn(Optional.of(new Usuario()));

        assertTrue(auth.existeUsuario(nombre, email));
    }

    @Test
    void testExisteUsuarioFalse() {
        // Comprueba que existeUsuario devuelve falso si ni el nombre ni el email existen.
        String nombre = "user";
        String email = "email";

        when(repo.findByNombre(nombre)).thenReturn(Optional.empty());
        when(repo.findByEmail(email)).thenReturn(Optional.empty());

        assertFalse(auth.existeUsuario(nombre, email));
    }

    @Test
    void testRegistrar() {
        // Comprueba que registrar guarda un usuario con datos correctos.
        String nombre = "user";
        String email = "email";
        String password = "pass";
        String hash = "hashed";

        when(encoder.encode(password)).thenReturn(hash);

        auth.registrar(nombre, email, password);

        verify(repo).save(argThat(u ->
            u.getNombre().equals(nombre) &&
            u.getEmail().equals(email) &&
            u.getPasswordHash().equals(hash)
        ));
    }
}
