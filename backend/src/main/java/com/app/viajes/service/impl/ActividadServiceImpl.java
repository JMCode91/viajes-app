package com.app.viajes.service.impl;

import com.app.viajes.dto.ActividadRequestDTO;
import com.app.viajes.dto.ActividadResponseDTO;
import com.app.viajes.dto.DocumentoDTO;
import com.app.viajes.exception.AccesoDenegadoException;
import com.app.viajes.exception.UsuarioNoEncontradoException;
import com.app.viajes.exception.ViajeNoEncontradoException;
import com.app.viajes.model.Actividad;
import com.app.viajes.model.Documento;
import com.app.viajes.model.Usuario;
import com.app.viajes.model.Viaje;
import com.app.viajes.repository.ActividadRepository;
import com.app.viajes.repository.DocumentoRepository;
import com.app.viajes.repository.UsuarioRepository;
import com.app.viajes.repository.UsuarioViajeRepository;
import com.app.viajes.repository.ViajeRepository;
import com.app.viajes.service.ActividadService;
import com.app.viajes.service.StorageService;
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
    private final DocumentoRepository documentoRepository;
    private final StorageService storageService;

    public ActividadServiceImpl(ActividadRepository actividadRepository,
                                ViajeRepository viajeRepository,
                                UsuarioRepository usuarioRepository,
                                UsuarioViajeRepository usuarioViajeRepository,
                                DocumentoRepository documentoRepository,
                                StorageService storageService) {
        this.actividadRepository = actividadRepository;
        this.viajeRepository = viajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioViajeRepository = usuarioViajeRepository;
        this.documentoRepository = documentoRepository;
        this.storageService = storageService;
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
        actividad.setDireccion(dto.direccion());
        actividad.setNotas(dto.notas());
        actividadRepository.save(actividad);

        return toResponseDTO(actividad);
    }

    @Override
    @Transactional
    public ActividadResponseDTO actualizarActividad(UUID viajeId, UUID actividadId, ActividadRequestDTO dto) {
        verificarPertenencia(viajeId);

        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new AccesoDenegadoException("Actividad no encontrada"));

        if (!actividad.getViaje().getId().equals(viajeId)) {
            throw new AccesoDenegadoException("La actividad no pertenece a este viaje");
        }

        actividad.setTitulo(dto.titulo());
        actividad.setDescripcion(dto.descripcion());
        actividad.setTipoActividad(dto.tipoActividad());
        actividad.setFechaHoraInicio(dto.fechaHoraInicio());
        actividad.setFechaHoraFin(dto.fechaHoraFin());
        actividad.setLatitud(dto.latitud());
        actividad.setLongitud(dto.longitud());
        actividad.setDireccion(dto.direccion());
        actividad.setNotas(dto.notas());
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

        // Borrado inteligente en cascada: se borran fisicamente de Cloudflare R2
        // todos los documentos de la actividad antes de eliminar el registro.
        for (Documento documento : actividad.getDocumentos()) {
            storageService.eliminarDocumento(documento.getUrlArchivo());
        }

        actividadRepository.delete(actividad);
    }

    @Override
    @Transactional
    public ActividadResponseDTO guardarDocumento(UUID viajeId, UUID actividadId,
                                                 String nombreOriginal, String urlArchivo) {
        verificarPertenencia(viajeId);

        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new AccesoDenegadoException("Actividad no encontrada"));

        if (!actividad.getViaje().getId().equals(viajeId)) {
            throw new AccesoDenegadoException("La actividad no pertenece a este viaje");
        }

        Documento documento = new Documento();
        documento.setActividad(actividad);
        documento.setNombreOriginal(nombreOriginal);
        documento.setUrlArchivo(urlArchivo);
        actividad.getDocumentos().add(documento);
        actividadRepository.save(actividad);

        return toResponseDTO(actividad);
    }

    @Override
    @Transactional
    public void eliminarDocumento(UUID viajeId, UUID actividadId, UUID documentoId) {
        verificarPertenencia(viajeId);

        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new AccesoDenegadoException("Documento no encontrado"));

        if (!documento.getActividad().getId().equals(actividadId)
                || !documento.getActividad().getViaje().getId().equals(viajeId)) {
            throw new AccesoDenegadoException("El documento no pertenece a esta actividad");
        }

        // Borrado inteligente: primero se elimina el archivo de Cloudflare R2
        // y, solo despues, el registro de la base de datos.
        storageService.eliminarDocumento(documento.getUrlArchivo());
        documentoRepository.delete(documento);
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
                actividad.getDireccion(),
                actividad.getNotas(),
                actividad.getDocumentos().stream()
                        .map(documento -> new DocumentoDTO(
                                documento.getId(),
                                documento.getNombreOriginal(),
                                documento.getUrlArchivo()))
                        .toList());
    }
}