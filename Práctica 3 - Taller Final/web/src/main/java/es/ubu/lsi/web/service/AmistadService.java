package es.ubu.lsi.web.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.ubu.lsi.web.entity.Amistad;
import es.ubu.lsi.web.entity.SolicitudAmistad;
import es.ubu.lsi.web.entity.SolicitudAmistad.EstadoSolicitud;
import es.ubu.lsi.web.entity.Usuario;
import es.ubu.lsi.web.repository.AmistadRepository;
import es.ubu.lsi.web.repository.SolicitudAmistadRepository;
import lombok.RequiredArgsConstructor;

/**
 * Servicio para gestionar las operaciones de amistad entre usuarios.
 * 
 * @author Ibai Moya Aroz
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class AmistadService {

    private final AmistadRepository amistadRepository;
    private final SolicitudAmistadRepository solicitudRepository;

    /**
     * Envía una solicitud de amistad de un usuario a otro.
     * 
     * @param remitente usuario que envía la solicitud
     * @param destinatario usuario que recibe la solicitud
     * @return la solicitud creada
     * @throws IllegalStateException si ya existe una solicitud o son amigos
     */
    @Transactional
    public SolicitudAmistad enviarSolicitud(Usuario remitente, Usuario destinatario) {
        // Verificar que no se envíe a sí mismo
        if (remitente.getId().equals(destinatario.getId())) {
            throw new IllegalArgumentException("No puedes enviarte una solicitud a ti mismo");
        }

        // Verificar si ya son amigos
        if (amistadRepository.sonAmigos(remitente, destinatario)) {
            throw new IllegalStateException("Ya sois amigos");
        }

        // Verificar si ya existe una solicitud pendiente
        if (solicitudRepository.existeSolicitudPendiente(remitente, destinatario)) {
            throw new IllegalStateException("Ya existe una solicitud pendiente");
        }

        // Crear nueva solicitud
        SolicitudAmistad solicitud = new SolicitudAmistad(remitente, destinatario);
        return solicitudRepository.save(solicitud);
    }

    /**
     * Acepta una solicitud de amistad.
     * 
     * @param solicitudId ID de la solicitud
     * @param usuario usuario que acepta (debe ser el destinatario)
     * @throws IllegalArgumentException si el usuario no es el destinatario
     */
    @Transactional
    public void aceptarSolicitud(Long solicitudId, Usuario usuario) {
        SolicitudAmistad solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        // Verificar que el usuario sea el destinatario
        if (!solicitud.getDestinatario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permiso para aceptar esta solicitud");
        }

        // Verificar que esté pendiente
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new IllegalStateException("La solicitud ya fue procesada");
        }

        // Actualizar solicitud
        solicitud.setEstado(EstadoSolicitud.ACEPTADA);
        solicitud.setFechaRespuesta(LocalDateTime.now());
        solicitudRepository.save(solicitud);

        // Crear relaciones de amistad bidireccionales
        Amistad amistad1 = new Amistad(solicitud.getRemitente(), solicitud.getDestinatario());
        Amistad amistad2 = new Amistad(solicitud.getDestinatario(), solicitud.getRemitente());
        
        amistadRepository.save(amistad1);
        amistadRepository.save(amistad2);
    }

    /**
     * Rechaza una solicitud de amistad.
     * 
     * @param solicitudId ID de la solicitud
     * @param usuario usuario que rechaza (debe ser el destinatario)
     */
    @Transactional
    public void rechazarSolicitud(Long solicitudId, Usuario usuario) {
        SolicitudAmistad solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        // Verificar que el usuario sea el destinatario
        if (!solicitud.getDestinatario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permiso para rechazar esta solicitud");
        }

        // Verificar que esté pendiente
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new IllegalStateException("La solicitud ya fue procesada");
        }

        // Actualizar solicitud
        solicitud.setEstado(EstadoSolicitud.RECHAZADA);
        solicitud.setFechaRespuesta(LocalDateTime.now());
        solicitudRepository.save(solicitud);
    }

    /**
     * Cancela una solicitud enviada.
     * 
     * @param solicitudId ID de la solicitud
     * @param usuario usuario que cancela (debe ser el remitente)
     */
    @Transactional
    public void cancelarSolicitud(Long solicitudId, Usuario usuario) {
        SolicitudAmistad solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        // Verificar que el usuario sea el remitente
        if (!solicitud.getRemitente().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permiso para cancelar esta solicitud");
        }

        // Verificar que esté pendiente
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new IllegalStateException("La solicitud ya fue procesada");
        }

        solicitudRepository.delete(solicitud);
    }

    /**
     * Elimina una amistad entre dos usuarios.
     * 
     * @param usuario usuario que elimina
     * @param amigoId ID del amigo a eliminar
     */
    @Transactional
    public void eliminarAmigo(Usuario usuario, Long amigoId) {
        Optional<Amistad> amistad = amistadRepository.findAll().stream()
            .filter(a -> a.getUsuario().getId().equals(usuario.getId()) 
                      && a.getAmigo().getId().equals(amigoId))
            .findFirst();

        if (amistad.isPresent()) {
            Usuario amigo = amistad.get().getAmigo();
            // Eliminar ambas direcciones
            amistadRepository.deleteByUsuarioAndAmigo(usuario, amigo);
            amistadRepository.deleteByUsuarioAndAmigo(amigo, usuario);
        }
    }

    /**
     * Obtiene la lista de amigos de un usuario.
     * 
     * @param usuario el usuario
     * @return lista de amistades
     */
    public List<Amistad> obtenerAmigos(Usuario usuario) {
        return amistadRepository.findByUsuario(usuario);
    }

    /**
     * Obtiene las solicitudes pendientes recibidas.
     * 
     * @param usuario el usuario
     * @return lista de solicitudes pendientes
     */
    public List<SolicitudAmistad> obtenerSolicitudesPendientes(Usuario usuario) {
        return solicitudRepository.findByDestinatarioAndEstado(usuario, EstadoSolicitud.PENDIENTE);
    }

    /**
     * Obtiene las solicitudes enviadas por un usuario.
     * 
     * @param usuario el usuario
     * @return lista de solicitudes enviadas
     */
    public List<SolicitudAmistad> obtenerSolicitudesEnviadas(Usuario usuario) {
        return solicitudRepository.findByRemitente(usuario);
    }

    /**
     * Cuenta las solicitudes pendientes de un usuario.
     * 
     * @param usuario el usuario
     * @return número de solicitudes pendientes
     */
    public long contarSolicitudesPendientes(Usuario usuario) {
        return solicitudRepository.countByDestinatarioAndEstado(usuario, EstadoSolicitud.PENDIENTE);
    }
}