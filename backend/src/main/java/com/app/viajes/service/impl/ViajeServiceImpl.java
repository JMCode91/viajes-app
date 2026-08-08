package com.app.viajes.service.impl;

import com.app.viajes.dto.InvitacionRequestDTO;
import com.app.viajes.dto.ViajeRequestDTO;
import com.app.viajes.dto.ViajeResponseDTO;
import com.app.viajes.exception.AccesoDenegadoException;
import com.app.viajes.exception.UsuarioNoEncontradoException;
import com.app.viajes.exception.ViajeNoEncontradoException;
import com.app.viajes.model.Usuario;
import com.app.viajes.model.UsuarioViaje;
import com.app.viajes.model.Viaje;
import com.app.viajes.model.enums.EstadoInvitacion;
import com.app.viajes.repository.ActividadRepository;
import com.app.viajes.repository.GastoRepository;
import com.app.viajes.repository.TareaChecklistRepository;
import com.app.viajes.repository.UsuarioRepository;
import com.app.viajes.repository.UsuarioViajeRepository;
import com.app.viajes.repository.ViajeRepository;
import com.app.viajes.service.ViajeService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implementacion del servicio de viajes e invitaciones.
 * La autorizacion se basa en la verificacion de pertenencia del usuario
 * autenticado al viaje (seguridad por propiedad de recursos).
 */
@Service
public class ViajeServiceImpl implements ViajeService {

    public static final String ROL_PROPIETARIO = "PROPIETARIO";
    public static final String ROL_INVITADO = "INVITADO";

    private final ViajeRepository viajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioViajeRepository usuarioViajeRepository;
    private final ActividadRepository actividadRepository;
    private final GastoRepository gastoRepository;
    private final TareaChecklistRepository tareaChecklistRepository;

    public ViajeServiceImpl(ViajeRepository viajeRepository,
                            UsuarioRepository usuarioRepository,
                            UsuarioViajeRepository usuarioViajeRepository,
                            ActividadRepository actividadRepository,
                            GastoRepository gastoRepository,
                            TareaChecklistRepository tareaChecklistRepository) {
        this.viajeRepository = viajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioViajeRepository = usuarioViajeRepository;
        this.actividadRepository = actividadRepository;
        this.gastoRepository = gastoRepository;
        this.tareaChecklistRepository = tareaChecklistRepository;
    }

    @Override
    @Transactional
    public ViajeResponseDTO crearViaje(ViajeRequestDTO request) {
        Usuario creador = obtenerUsuarioAutenticado();

        Viaje viaje = new Viaje();
        viaje.setTitulo(request.titulo());
        viaje.setFechaInicio(request.fechaInicio());
        viaje.setFechaFin(request.fechaFin());
        viaje.setPresupuestoBase(request.presupuestoBase());
        viajeRepository.save(viaje);

        UsuarioViaje pertenencia = new UsuarioViaje();
        pertenencia.setUsuario(creador);
        pertenencia.setViaje(viaje);
        pertenencia.setEstadoInvitacion(EstadoInvitacion.ACEPTADA);
        pertenencia.setRolEnViaje(ROL_PROPIETARIO);
        usuarioViajeRepository.save(pertenencia);

        return new ViajeResponseDTO(
                viaje.getId(),
                viaje.getTitulo(),
                viaje.getFechaInicio(),
                viaje.getFechaFin(),
                viaje.getPresupuestoBase(),
                ROL_PROPIETARIO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViajeResponseDTO> obtenerMisViajes() {
        Usuario usuario = obtenerUsuarioAutenticado();

        return usuarioViajeRepository.findByUsuarioIdAndEstadoInvitacion(
                        usuario.getId(), EstadoInvitacion.ACEPTADA)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void invitarUsuario(UUID viajeId, InvitacionRequestDTO request) {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();

        Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new ViajeNoEncontradoException("Viaje no encontrado"));

        // Solo el PROPIETARIO puede invitar.
        UsuarioViaje pertenenciaAutenticado = usuarioViajeRepository
                .findByUsuarioIdAndViajeId(usuarioAutenticado.getId(), viajeId)
                .orElseThrow(() -> new AccesoDenegadoException(
                        "No tiene acceso a este viaje"));
        if (!ROL_PROPIETARIO.equals(pertenenciaAutenticado.getRolEnViaje())) {
            throw new AccesoDenegadoException(
                    "Solo el propietario del viaje puede enviar invitaciones");
        }

        Usuario invitado = usuarioRepository.findByEmail(request.emailInvitado())
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe un usuario con ese email"));

        if (usuarioViajeRepository.existsByUsuarioIdAndViajeId(invitado.getId(), viajeId)) {
            throw new AccesoDenegadoException("El usuario ya esta vinculado a este viaje");
        }

        UsuarioViaje nuevaPertenencia = new UsuarioViaje();
        nuevaPertenencia.setUsuario(invitado);
        nuevaPertenencia.setViaje(viaje);
        nuevaPertenencia.setEstadoInvitacion(EstadoInvitacion.PENDIENTE);
        nuevaPertenencia.setRolEnViaje(ROL_INVITADO);
        usuarioViajeRepository.save(nuevaPertenencia);
    }

    @Override
    @Transactional
    public void responderInvitacion(UUID viajeId, boolean aceptar) {
        Usuario usuario = obtenerUsuarioAutenticado();

        UsuarioViaje pertenencia = usuarioViajeRepository
                .findByUsuarioIdAndViajeId(usuario.getId(), viajeId)
                .orElseThrow(() -> new AccesoDenegadoException(
                        "No tiene una invitacion pendiente para este viaje"));

        if (pertenencia.getEstadoInvitacion() != EstadoInvitacion.PENDIENTE) {
            throw new AccesoDenegadoException(
                    "Esta invitacion ya ha sido respondida");
        }

        pertenencia.setEstadoInvitacion(aceptar
                ? EstadoInvitacion.ACEPTADA
                : EstadoInvitacion.RECHAZADA);
        usuarioViajeRepository.save(pertenencia);
    }

    @Override
    @Transactional
    public void eliminarViaje(UUID viajeId) {
        Usuario usuario = obtenerUsuarioAutenticado();

        // Solo el PROPIETARIO puede eliminar el viaje.
        UsuarioViaje pertenencia = usuarioViajeRepository
                .findByUsuarioIdAndViajeId(usuario.getId(), viajeId)
                .orElseThrow(() -> new AccesoDenegadoException(
                        "No tiene acceso a este viaje"));
        if (!ROL_PROPIETARIO.equals(pertenencia.getRolEnViaje())) {
            throw new AccesoDenegadoException(
                    "Solo el propietario del viaje puede eliminarlo");
        }

        // Borrado en cascada manual: los hijos mapean @ManyToOne -> Viaje
        // sin colecciones @OneToMany (no hay OrphanRemoval), por lo que se
        // eliminan explicitamente para evitar violacion de Clave Foranea.
        tareaChecklistRepository.deleteByViajeId(viajeId);
        gastoRepository.deleteByViajeId(viajeId);
        actividadRepository.deleteByViajeId(viajeId);
        usuarioViajeRepository.deleteByViajeId(viajeId);
        viajeRepository.deleteById(viajeId);
    }

    private Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario autenticado no encontrado"));
    }

    private ViajeResponseDTO toResponseDTO(UsuarioViaje pertenencia) {
        Viaje viaje = pertenencia.getViaje();
        return new ViajeResponseDTO(
                viaje.getId(),
                viaje.getTitulo(),
                viaje.getFechaInicio(),
                viaje.getFechaFin(),
                viaje.getPresupuestoBase(),
                pertenencia.getRolEnViaje());
    }
}