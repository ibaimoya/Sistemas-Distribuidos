package es.ubu.lsi.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.ubu.lsi.web.entity.SolicitudAmistad;
import es.ubu.lsi.web.entity.SolicitudAmistad.EstadoSolicitud;
import es.ubu.lsi.web.entity.Usuario;

/**
 * Repositorio para gestionar las operaciones de solicitudes de amistad.
 * 
 * @author Ibai Moya Aroz
 * 
 * @version 1.0
 * @since 1.0
 */
public interface SolicitudAmistadRepository extends JpaRepository<SolicitudAmistad, Long> {
    
    /**
     * Encuentra todas las solicitudes pendientes recibidas por un usuario.
     * 
     * @param destinatario el usuario que recibe las solicitudes
     * @return lista de solicitudes pendientes
     */
    List<SolicitudAmistad> findByDestinatarioAndEstado(Usuario destinatario, EstadoSolicitud estado);
    
    /**
     * Encuentra todas las solicitudes enviadas por un usuario.
     * 
     * @param remitente el usuario que envía las solicitudes
     * @return lista de solicitudes enviadas
     */
    List<SolicitudAmistad> findByRemitente(Usuario remitente);
    
    /**
     * Busca una solicitud específica entre dos usuarios.
     * 
     * @param remitente usuario que envía
     * @param destinatario usuario que recibe
     * @return la solicitud si existe
     */
    Optional<SolicitudAmistad> findByRemitenteAndDestinatario(Usuario remitente, Usuario destinatario);
    
    /**
     * Verifica si existe una solicitud pendiente entre dos usuarios (en cualquier dirección).
     * 
     * @param usuario1 primer usuario
     * @param usuario2 segundo usuario
     * @return true si existe solicitud pendiente
     */
    @Query("SELECT COUNT(s) > 0 FROM SolicitudAmistad s WHERE " +
           "s.estado = 'PENDIENTE' AND " +
           "((s.remitente = :usuario1 AND s.destinatario = :usuario2) OR " +
           "(s.remitente = :usuario2 AND s.destinatario = :usuario1))")
    boolean existeSolicitudPendiente(@Param("usuario1") Usuario usuario1, 
                                   @Param("usuario2") Usuario usuario2);
    
    /**
     * Cuenta las solicitudes pendientes recibidas por un usuario.
     * 
     * @param destinatario el usuario
     * @return número de solicitudes pendientes
     */
    long countByDestinatarioAndEstado(Usuario destinatario, EstadoSolicitud estado);
}