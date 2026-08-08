package com.app.viajes.service.impl;

import com.app.viajes.dto.TareaChecklistRequestDTO;
import com.app.viajes.dto.TareaChecklistResponseDTO;
import com.app.viajes.exception.AccesoDenegadoException;
import com.app.viajes.exception.UsuarioNoEncontradoException;
import com.app.viajes.exception.ViajeNoEncontradoException;
import com.app.viajes.model.TareaChecklist;
import com.app.viajes.model.Usuario;
import com.app.viajes.model.Viaje;
import com.app.viajes.repository.TareaChecklistRepository;
import com.app.viajes.repository.UsuarioRepository;
import com.app.viajes.repository.UsuarioViajeRepository;
import com.app.viajes.repository.ViajeRepository;
import com.app.viajes.service.TareaChecklistService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implementacion del servicio de checklist (tareas). Valida la pertenencia
 * del usuario autenticado al viaje en cada operacion y filtra las tareas
 * privadas de otros usuarios al listar.
 */
@Service
public class TareaChecklistServiceImpl implements TareaChecklistService {

    private final TareaChecklistRepository tareaRepository;
    private final ViajeRepository viajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioViajeRepository usuarioViajeRepository;

    public TareaChecklistServiceImpl(TareaChecklistRepository tareaRepository,
                                     ViajeRepository viajeRepository,
                                     UsuarioRepository usuarioRepository,
                                     UsuarioViajeRepository usuarioViajeRepository) {
        this.tareaRepository = tareaRepository;
        this.viajeRepository = viajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioViajeRepository = usuarioViajeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TareaChecklistResponseDTO> obtenerTareas(UUID viajeId) {
        verificarPertenencia(viajeId);
        Usuario usuario = obtenerUsuarioAutenticado();
        return tareaRepository.findVisiblesPorViajeYUsuario(viajeId, usuario.getId())
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public TareaChecklistResponseDTO crearTarea(UUID viajeId, TareaChecklistRequestDTO dto) {
        verificarPertenencia(viajeId);
        Usuario creador = obtenerUsuarioAutenticado();

        Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new ViajeNoEncontradoException("Viaje no encontrado"));

        TareaChecklist tarea = new TareaChecklist();
        tarea.setViaje(viaje);
        tarea.setCreador(creador);
        tarea.setContenido(dto.contenido());
        tarea.setCompletada(Boolean.FALSE);
        tarea.setEsPrivada(dto.esPrivada());
        if (dto.asignadoId() != null) {
            tarea.setAsignado(resolverAsignado(dto.asignadoId(), viajeId));
        }
        tareaRepository.save(tarea);

        return toResponseDTO(tarea);
    }

    @Override
    @Transactional
    public TareaChecklistResponseDTO cambiarEstado(UUID viajeId, Long tareaId, boolean completada) {
        verificarPertenencia(viajeId);
        TareaChecklist tarea = obtenerTareaDelViaje(viajeId, tareaId);
        tarea.setCompletada(completada);
        tareaRepository.save(tarea);
        return toResponseDTO(tarea);
    }

    @Override
    @Transactional
    public TareaChecklistResponseDTO asignarUsuario(UUID viajeId, Long tareaId, UUID asignadoId) {
        verificarPertenencia(viajeId);
        TareaChecklist tarea = obtenerTareaDelViaje(viajeId, tareaId);
        tarea.setAsignado(asignadoId != null
                ? resolverAsignado(asignadoId, viajeId)
                : null);
        tareaRepository.save(tarea);
        return toResponseDTO(tarea);
    }

    @Override
    @Transactional
    public void eliminarTarea(UUID viajeId, Long tareaId) {
        verificarPertenencia(viajeId);
        TareaChecklist tarea = obtenerTareaDelViaje(viajeId, tareaId);
        tareaRepository.delete(tarea);
    }

    private TareaChecklist obtenerTareaDelViaje(UUID viajeId, Long tareaId) {
        TareaChecklist tarea = tareaRepository.findById(tareaId)
                .orElseThrow(() -> new AccesoDenegadoException("Tarea no encontrada"));
        if (!tarea.getViaje().getId().equals(viajeId)) {
            throw new AccesoDenegadoException("La tarea no pertenece a este viaje");
        }
        return tarea;
    }

    private Usuario resolverAsignado(UUID asignadoId, UUID viajeId) {
        Usuario asignado = usuarioRepository.findById(asignadoId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe un usuario con ese id"));
        if (!usuarioViajeRepository.existsByUsuarioIdAndViajeId(asignadoId, viajeId)) {
            throw new AccesoDenegadoException(
                    "El usuario asignado no pertenece a este viaje");
        }
        return asignado;
    }

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

    private TareaChecklistResponseDTO toResponseDTO(TareaChecklist tarea) {
        return new TareaChecklistResponseDTO(
                tarea.getId(),
                tarea.getContenido(),
                Boolean.TRUE.equals(tarea.getCompletada()),
                Boolean.TRUE.equals(tarea.getEsPrivada()),
                tarea.getCreador().getNombre(),
                tarea.getAsignado() != null ? tarea.getAsignado().getNombre() : null);
    }
}