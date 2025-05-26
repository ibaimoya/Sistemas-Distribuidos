package es.ubu.lsi.SpringPythonAPI.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    /**
     * Constructor por defecto necesario para JPA.
     * Se utiliza para crear instancias de la clase sin inicializar los atributos.
     */
    public Usuario() { }

    /**
     * Constructor que inicializa los atributos de la clase.
     * Se utiliza para crear instancias de la clase con los atributos inicializados.
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

    /* Getters y setters. */
    public Long getId()                     { return id; }
    public String getNombre()               { return nombre; }
    public void   setNombre(String n)       { this.nombre = n; }
    public String getEmail()                { return email; }
    public void   setEmail(String e)        { this.email = e; }
    public String getPasswordHash()         { return passwordHash; }
    public void   setPasswordHash(String h) { this.passwordHash = h; }
}
