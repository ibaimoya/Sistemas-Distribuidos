package es.ubu.lsi.web.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    // Getters adicionales para compatibilidad
    public String getEmail() {
        return username; // El frontend puede enviar email como username
    }

    public void setEmail(String email) {
        this.username = email;
    }
}