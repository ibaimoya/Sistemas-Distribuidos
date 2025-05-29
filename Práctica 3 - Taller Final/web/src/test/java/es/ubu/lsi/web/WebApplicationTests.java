package es.ubu.lsi.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;

import es.ubu.lsi.web.service.AuthService;

@SpringBootTest
class WebApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void testInitUserExists() throws Exception {
		// Comprueba que no registra si el usuario ya existe.
		AuthService auth = mock(AuthService.class);
		when(auth.existeUsuario("admin", "admin@admin.com")).thenReturn(true);

		CommandLineRunner runner = new WebApplication().init(auth);
		runner.run();

		verify(auth).existeUsuario("admin", "admin@admin.com");
		verify(auth, never()).registrar(any(), any(), any());
	}

	@Test
	void testInitRegisterUser() throws Exception {
		// Comprueba que registra si el usuario no existe.
		AuthService auth = mock(AuthService.class);
		when(auth.existeUsuario("admin", "admin@admin.com")).thenReturn(false);

		CommandLineRunner runner = new WebApplication().init(auth);
		runner.run();

		verify(auth).existeUsuario("admin", "admin@admin.com");
		verify(auth).registrar("admin", "admin@admin.com", "admin");
	}
}