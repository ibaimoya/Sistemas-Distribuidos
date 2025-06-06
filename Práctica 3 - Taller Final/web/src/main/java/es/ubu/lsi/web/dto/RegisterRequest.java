package es.ubu.lsi.web.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) para la solicitud de registro de un nuevo usuario.
 * Contiene el nombre de usuario, email, contraseña y confirmación de contraseña.
 * 
 * @author Ibai Moya Aroz
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    /** Nombre de usuario. */
    @NotBlank(message = "El usuario es obligatorio")
    @JsonAlias("username")
    private String usuario;

    /** Email del usuario. */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    /** Contraseña del usuario. */
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    /** Confirmación de la contraseña. */
    @NotBlank(message = "Debe confirmar la contraseña")
    private String confirm;
}