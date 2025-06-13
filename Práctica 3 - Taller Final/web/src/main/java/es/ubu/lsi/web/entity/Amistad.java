package es.ubu.lsi.web.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa una relación de amistad entre dos usuarios.
 * Cuando se acepta una solicitud, se crean dos registros de amistad (uno por cada dirección).
 * 
 * @author Ibai Moya Aroz
 * 
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"usuario_id", "amigo_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class Amistad {

    /**
     * ID único de la amistad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario que tiene al amigo.
     */
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /**
     * El amigo del usuario.
     */
    @ManyToOne
    @JoinColumn(name = "amigo_id", nullable = false)
    private Usuario amigo;

    /**
     * Fecha en que se estableció la amistad.
     */
    @Column(nullable = false)
    private LocalDateTime fechaAmistad;

    /**
     * Constructor para crear una nueva amistad.
     * 
     * @param usuario el usuario que tiene al amigo
     * @param amigo el amigo del usuario
     */
    public Amistad(Usuario usuario, Usuario amigo) {
        this.usuario = usuario;
        this.amigo = amigo;
    }

    /**
     * Establece la fecha de amistad antes de persistir.
     */
    @PrePersist
    protected void onCreate() {
        fechaAmistad = LocalDateTime.now();
    }
}