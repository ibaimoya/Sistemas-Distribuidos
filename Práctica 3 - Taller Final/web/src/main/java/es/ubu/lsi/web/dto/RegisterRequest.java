package es.ubu.lsi.web.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
public class RegisterRequest {

    @NotBlank(message = "El usuario es obligatorio")
    private String usuario;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotBlank(message = "Debe confirmar la contraseña")
    private String confirm;

    // Getters adicionales para compatibilidad con el frontend
    public String getUsername() {
        return usuario;
    }

    public void setUsername(String username) {
        this.usuario = username;
    }
}