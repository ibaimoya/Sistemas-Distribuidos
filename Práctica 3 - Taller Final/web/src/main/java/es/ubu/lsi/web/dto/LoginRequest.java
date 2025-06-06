package es.ubu.lsi.web.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) para la solicitud de inicio de sesión.
 * Contiene el nombre de usuario y la contraseña.
 * 
 * @author Ibai Moya Aroz
 * 
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    /** Nombre de usuario. */
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @JsonAlias("email")
    private String username;

    /** Contraseña. */
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}