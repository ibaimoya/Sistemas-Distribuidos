package es.ubu.lsi.web.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/* Representa los datos del formulario de registro con validación. */
/**
 * Clase que representa un formulario de registro de usuario.
 * Contiene los campos necesarios para el registro y sus validaciones.
 * 
 * @author Ibai Moya Aroz
 *
 * @version 1.0
 * @since 1.0
 */
public class RegisterForm {

    /** Nombre de usuario. */
    @NotBlank(message = "El nombre de usuario es obligatorio.")
    private String usuario;

    /** Correo electrónico. */
    @NotBlank(message = "El correo electrónico es obligatorio.")
    @Email(message = "El correo electrónico no tiene formato válido.")
    private String email;

    /** Contraseña. */
    @NotBlank(message = "La contraseña es obligatoria.")
    private String password;

    /** Confirmación de contraseña. */
    @NotBlank(message = "Confirma la contraseña.")
    private String confirm;

    /* Getters y setters. */
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String u) {
        this.usuario = u;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String e) {
        this.email = e;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String p) {
        this.password = p;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String c) {
        this.confirm = c;
    }
}