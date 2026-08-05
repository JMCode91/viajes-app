package com.app.viajes.service.impl;

import com.app.viajes.dto.ActividadRequestDTO;
import com.app.viajes.dto.ActividadResponseDTO;
import com.app.viajes.exception.AccesoDenegadoException;
import com.app.viajes.exception.UsuarioNoEncontradoException;
import com.app.viajes.exception.ViajeNoEncontradoException;
import com.app.viajes.model.Actividad;
import com.app.viajes.model.Usuario;
import com.app.viajes.model.Viaje;
import com.app.viajes.repository.ActividadRepository;
import com.app.viajes.repository.UsuarioRepository;
import com.app.viajes.repository.UsuarioViajeRepository;
import com.app.viajes.repository.ViajeRepository;
import com.app.viajes.service.ActividadService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementacion del servicio de actividades.
 * Antes de cualquier operacion se verifica que el usuario autenticado
 * pertenece al viaje (seguridad por aislamiento de datos).
 */
@Service
public class ActividadServiceImpl implements ActividadService {

    private final ActividadRepository actividadRepository;
    private final ViajeRepository viajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioViajeRepository usuarioViajeRepository;

    public ActividadServiceImpl(ActividadRepository actividadRepository,
                                ViajeRepository viajeRepository,
                                UsuarioRepository usuarioRepository,
                                UsuarioViajeRepository usuarioViajeRepository) {
        this.actividadRepository = actividadRepository;
        this.viajeRepository = viajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioViajeRepository = usuarioViajeRepository;
    }

    @Override
    @Transactional
    public ActividadResponseDTO crearActividad(UUID viajeId, ActividadRequestDTO dto) {
        verificarPertenencia(viajeId);

        Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new ViajeNoEncontradoException("Viaje no encontrado"));

        Actividad actividad = new Actividad();
        actividad.setViaje(viaje);
        actividad.setTitulo(dto.titulo());
        actividad.setDescripcion(dto.descripcion());
        actividad.setTipoActividad(dto.tipoActividad());
        actividad.setFechaHoraInicio(dto.fechaHoraInicio());
        actividad.setFechaHoraFin(dto.fechaHoraFin());
        actividad.setLatitud(dto.latitud());
        actividad.setLongitud(dto.longitud());
        actividad.setUrlDocumento(dto.urlDocumento());
        actividadRepository.save(actividad);

        return toResponseDTO(actividad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActividadResponseDTO> obtenerActividadesPorViaje(UUID viajeId) {
        verificarPertenencia(viajeId);

        return actividadRepository.findByViajeIdOrderByFechaHoraInicioAsc(viajeId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActividadResponseDTO> obtenerTimelinePorFecha(UUID viajeId, LocalDate fecha) {
        verificarPertenencia(viajeId);

        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay().minusNanos(1);

        return actividadRepository.findByViajeIdAndFecha(viajeId, inicio, fin)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void eliminarActividad(UUID viajeId, UUID actividadId) {
        verificarPertenencia(viajeId);

        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new AccesoDenegadoException("Actividad no encontrada"));

        // Verifica que la actividad pertenezca al viaje indicado.
        if (!actividad.getViaje().getId().equals(viajeId)) {
            throw new AccesoDenegadoException("La actividad no pertenece a este viaje");
        }

        actividadRepository.delete(actividad);
    }

    /**
     * Valida que el usuario autenticado pertenece al viaje indicado.
     */
    private void verificarPertenencia(UUID viajeId) {
        Usuario usuario = obtenerUsuarioAutenticado();

        if (!usuarioViajeRepository.existsByUsuarioIdAndViajeId(usuario.getId(), viajeId)) {
            throw new AccesoDenegadoException("No tiene acceso a este viaje");
        }
    }

    private Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario autenticado no encontrado"));
    }

    private ActividadResponseDTO toResponseDTO(Actividad actividad) {
        return new ActividadResponseDTO(
                actividad.getId(),
                actividad.getViaje().getId(),
                actividad.getTitulo(),
                actividad.getDescripcion(),
                actividad.getTipoActividad(),
                actividad.getFechaHoraInicio(),
                actividad.getFechaHoraFin(),
                actividad.getLatitud(),
                actividad.getLongitud(),
                actividad.getUrlDocumento());
    }
}