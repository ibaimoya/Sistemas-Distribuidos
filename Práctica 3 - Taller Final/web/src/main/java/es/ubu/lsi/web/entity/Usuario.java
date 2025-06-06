package es.ubu.lsi.web.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Clase que representa a un usuario en la base de datos.
 * Se utiliza para almacenar la información de los usuarios
 * que se registran en la aplicación.
 *
 * @author Ibai Moya Aroz
 *
 * @version 1.0
 * @since 1.0
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    /**
     * ID único del usuario.
     * Deja que el ID sea generado automáticamente por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de usuario.
     * Este campo es obligatorio y debe ser único.
     */
    @Column(nullable = false, unique = true)
    private String nombre;

    /**
     * Email del usuario.
     * Este campo es obligatorio y debe ser único.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Contraseña hasheada del usuario.
     * Este campo es obligatorio y no puede ser nulo.
     */
    @Column(nullable = false)
    private String passwordHash;

    /**
     * Rol del usuario en la aplicación.
     * Por defecto, el rol es 'USER'.
     * Puede ser 'USER' o 'ADMIN'.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    /**
     * Constructor que inicializa los atributos de la clase.
     * Se utiliza para crear instancias de la clase con los atributos inicializados.
     * Deja que el ID sea generado automáticamente por la base de datos.
     * 
     * @param nombre Nombre de usuario
     * @param email Email del usuario
     * @param passwordHash Contraseña hasheada del usuario
     */
    public Usuario(String nombre, String email, String passwordHash) {
        this.nombre = nombre;
        this.email = email;
        this.passwordHash = passwordHash;
    }
}
