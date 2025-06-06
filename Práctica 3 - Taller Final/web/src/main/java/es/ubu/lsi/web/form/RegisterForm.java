package es.ubu.lsi.web.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}