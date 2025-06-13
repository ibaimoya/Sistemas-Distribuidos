package es.ubu.lsi.web.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * Entidad que representa una solicitud de amistad entre dos usuarios.
 * 
 * @author Ibai Moya Aroz
 * 
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"remitente_id", "destinatario_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class SolicitudAmistad {

    /**
     * ID único de la solicitud.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario que envía la solicitud.
     */
    @ManyToOne
    @JoinColumn(name = "remitente_id", nullable = false)
    private Usuario remitente;

    /**
     * Usuario que recibe la solicitud.
     */
    @ManyToOne
    @JoinColumn(name = "destinatario_id", nullable = false)
    private Usuario destinatario;

    /**
     * Estado de la solicitud.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    /**
     * Fecha de creación de la solicitud.
     */
    @Column(nullable = false)
    private LocalDateTime fechaSolicitud;

    /**
     * Fecha de respuesta (aceptación o rechazo).
     */
    @Column
    private LocalDateTime fechaRespuesta;

    /**
     * Constructor para crear una nueva solicitud.
     * 
     * @param remitente el usuario que envía la solicitud
     * @param destinatario el usuario que recibe la solicitud
     */
    public SolicitudAmistad(Usuario remitente, Usuario destinatario) {
        this.remitente = remitente;
        this.destinatario = destinatario;
    }

    /**
     * Establece la fecha de solicitud antes de persistir.
     */
    @PrePersist
    protected void onCreate() {
        fechaSolicitud = LocalDateTime.now();
    }

    /**
     * Enumeración para los estados de la solicitud.
     */
    public enum EstadoSolicitud {
        PENDIENTE,
        ACEPTADA,
        RECHAZADA
    }
}