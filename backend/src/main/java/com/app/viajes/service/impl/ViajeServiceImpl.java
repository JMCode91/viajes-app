package com.app.viajes.service.impl;

import com.app.viajes.dto.InvitacionRequestDTO;
import com.app.viajes.dto.ParticipanteResponseDTO;
import com.app.viajes.dto.ViajeRequestDTO;
import com.app.viajes.dto.ViajeResponseDTO;
import com.app.viajes.dto.ViajeUpdateDTO;
import com.app.viajes.exception.AccesoDenegadoException;
import com.app.viajes.exception.UsuarioNoEncontradoException;
import com.app.viajes.exception.ViajeNoEncontradoException;
import com.app.viajes.model.Documento;
import com.app.viajes.model.Foto;
import com.app.viajes.model.Gasto;
import com.app.viajes.model.Usuario;
import com.app.viajes.model.UsuarioViaje;
import com.app.viajes.model.Viaje;
import com.app.viajes.model.enums.EstadoInvitacion;
import com.app.viajes.repository.ActividadRepository;
import com.app.viajes.repository.AlbumRepository;
import com.app.viajes.repository.DeudaGastoRepository;
import com.app.viajes.repository.DocumentoRepository;
import com.app.viajes.repository.FotoRepository;
import com.app.viajes.repository.GastoRepository;
import com.app.viajes.repository.TareaChecklistRepository;
import com.app.viajes.repository.UsuarioRepository;
import com.app.viajes.repository.UsuarioViajeRepository;
import com.app.viajes.repository.ViajeRepository;
import com.app.viajes.service.StorageService;
import com.app.viajes.service.ViajeService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
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
    private final DeudaGastoRepository deudaGastoRepository;
    private final TareaChecklistRepository tareaChecklistRepository;
    private final DocumentoRepository documentoRepository;
    private final FotoRepository fotoRepository;
    private final AlbumRepository albumRepository;
    private final StorageService storageService;

    public ViajeServiceImpl(ViajeRepository viajeRepository,
                            UsuarioRepository usuarioRepository,
                            UsuarioViajeRepository usuarioViajeRepository,
                            ActividadRepository actividadRepository,
                            GastoRepository gastoRepository,
                            DeudaGastoRepository deudaGastoRepository,
                            TareaChecklistRepository tareaChecklistRepository,
                            DocumentoRepository documentoRepository,
                            FotoRepository fotoRepository,
                            AlbumRepository albumRepository,
                            StorageService storageService) {
        this.viajeRepository = viajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioViajeRepository = usuarioViajeRepository;
        this.actividadRepository = actividadRepository;
        this.gastoRepository = gastoRepository;
        this.deudaGastoRepository = deudaGastoRepository;
        this.tareaChecklistRepository = tareaChecklistRepository;
        this.documentoRepository = documentoRepository;
        this.fotoRepository = fotoRepository;
        this.albumRepository = albumRepository;
        this.storageService = storageService;
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

        crearPertenencia(creador, viaje, EstadoInvitacion.ACEPTADA, ROL_PROPIETARIO);

        return toResponseDTO(viaje, ROL_PROPIETARIO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViajeResponseDTO> obtenerMisViajes() {
        Usuario usuario = obtenerUsuarioAutenticado();

        return usuarioViajeRepository.findByUsuarioIdAndEstadoInvitacion(
                        usuario.getId(), EstadoInvitacion.ACEPTADA)
                .stream()
                // Borrado logico: si el usuario abandono el viaje (activo=false),
                // este deja de aparecer en su listado.
                .filter(UsuarioViaje::isActivo)
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void invitarUsuario(UUID viajeId, InvitacionRequestDTO request) {
        verificarOrganizador(viajeId);

        Viaje viaje = obtenerViaje(viajeId);

        Usuario invitado = usuarioRepository.findByEmail(request.emailInvitado())
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe un usuario con ese email"));

        if (usuarioViajeRepository.existsByUsuarioIdAndViajeId(invitado.getId(), viajeId)) {
            throw new AccesoDenegadoException("El usuario ya esta vinculado a este viaje");
        }

        crearPertenencia(invitado, viaje, EstadoInvitacion.PENDIENTE, ROL_INVITADO);
    }

    @Override
    @Transactional
    public void responderInvitacion(UUID viajeId, boolean aceptar) {
        Usuario usuario = obtenerUsuarioAutenticado();

        Optional<UsuarioViaje> pertenenciaOpt = usuarioViajeRepository
                .findByUsuarioIdAndViajeId(usuario.getId(), viajeId);

        // Flujo "unirse a un viaje con el UUID de invitacion": cualquier
        // usuario autenticado puede unirse utilizando el codigo (id del viaje)
        // compartido por el propietario, aun sin haber sido invitado antes.
        if (pertenenciaOpt.isEmpty() && aceptar) {
            Viaje viaje = obtenerViaje(viajeId);
            crearPertenencia(usuario, viaje, EstadoInvitacion.ACEPTADA, ROL_INVITADO);
            return;
        }

        UsuarioViaje pertenencia = pertenenciaOpt
                .orElseThrow(() -> new AccesoDenegadoException(
                        "No tiene una invitacion pendiente para este viaje"));

        if (pertenencia.getEstadoInvitacion() == EstadoInvitacion.ACEPTADA) {
            // Reunion: si el usuario habia abandonado el viaje (borrado logico),
            // volver a unirse con el codigo lo reactiva.
            if (!pertenencia.isActivo()) {
                pertenencia.setActivo(true);
                usuarioViajeRepository.save(pertenencia);
            }
            return;
        }

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
    public void abandonarViaje(UUID viajeId) {
        UsuarioViaje pertenencia = obtenerPertenencia(viajeId);

        if (ROL_PROPIETARIO.equals(pertenencia.getRolEnViaje())) {
            throw new AccesoDenegadoException(
                    "El propietario no puede abandonar el viaje");
        }

        // Borrado logico: el invitado deja de ver el viaje en su listado,
        // se conserva la integridad historica de gastos y listas.
        pertenencia.setActivo(false);
        usuarioViajeRepository.save(pertenencia);
    }

    @Override
    @Transactional
    public void eliminarViaje(UUID viajeId) {
        // Solo el PROPIETARIO puede eliminar el viaje.
        verificarOrganizador(viajeId);

        // Borrado en cascada manual: los hijos mapean @ManyToOne -> Viaje
        // sin colecciones @OneToMany (no hay OrphanRemoval), por lo que se
        // eliminan explicitamente para evitar violacion de Clave Foranea.
        // El orden preserva las dependencias: checklist, gastos, documentos
        // y actividades antes que pertenencias; fotos antes que albumes.
        tareaChecklistRepository.deleteByViajeId(viajeId);
        purgarGastosYDeudas(viajeId);
        purgarDocumentosDelViaje(viajeId);
        actividadRepository.deleteByViajeId(viajeId);
        usuarioViajeRepository.deleteByViajeId(viajeId);
        purgarFotosYAlbumes(viajeId);

        viajeRepository.deleteById(viajeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipanteResponseDTO> obtenerParticipantes(UUID viajeId) {
        // Seguridad por propiedad de recursos: solo un participante activo y
        // ACEPTADO de este viaje puede listar a los demas.
        obtenerPertenenciaActiva(viajeId);

        return usuarioViajeRepository.findByViajeId(viajeId).stream()
                .filter(pertenencia ->
                        pertenencia.getEstadoInvitacion() == EstadoInvitacion.ACEPTADA)
                .map(this::toParticipanteDTO)
                .toList();
    }

    @Override
    @Transactional
    public ViajeResponseDTO actualizarViaje(UUID viajeId, ViajeUpdateDTO request) {
        verificarOrganizador(viajeId);

        Viaje viaje = obtenerViaje(viajeId);

        viaje.setTitulo(request.titulo());
        viaje.setFechaInicio(request.fechaInicio());
        viaje.setFechaFin(request.fechaFin());
        // El presupuesto global es opcional: si no viaja en el request,
        // se conserva el valor actual del viaje.
        if (request.presupuestoGlobal() != null) {
            viaje.setPresupuestoGlobal(request.presupuestoGlobal());
        }
        viajeRepository.save(viaje);

        return toResponseDTO(viaje, ROL_PROPIETARIO);
    }

    @Override
    @Transactional
    public ViajeResponseDTO actualizarFotoPortada(UUID viajeId, MultipartFile archivo) {
        verificarOrganizador(viajeId);

        Viaje viaje = obtenerViaje(viajeId);

        // Borrado inteligente: si ya habia portada, se purga fisicamente de
        // Cloudflare R2 antes de subir la nueva (evita archivos huerfanos).
        storageService.eliminarDocumento(viaje.getFotoPortada());

        String url = storageService.subirImagen(archivo);
        viaje.setFotoPortada(url);
        viajeRepository.save(viaje);

        return toResponseDTO(viaje, ROL_PROPIETARIO);
    }

    @Override
    @Transactional
    public void expulsarParticipante(UUID viajeId, UUID usuarioId) {
        verificarOrganizador(viajeId);

        // Un organizador no puede expulsarse a si mismo por esta via.
        Usuario autenticado = obtenerUsuarioAutenticado();
        if (autenticado.getId().equals(usuarioId)) {
            throw new AccesoDenegadoException(
                    "El organizador no puede expulsarse a si mismo");
        }

        // Abandono forzado: se elimina el vinculo usuario-viaje.
        UsuarioViaje pertenencia = usuarioViajeRepository
                .findByUsuarioIdAndViajeId(usuarioId, viajeId)
                .orElseThrow(() -> new AccesoDenegadoException(
                        "El usuario no pertenece a este viaje"));

        usuarioViajeRepository.delete(pertenencia);
    }

    @Override
    @Transactional
    public ParticipanteResponseDTO actualizarPresupuestoIndividual(
            UUID viajeId, BigDecimal presupuesto) {
        // Seguridad por propiedad de recursos: solo un participante activo y
        // ACEPTADO de este viaje puede actualizar su propio presupuesto.
        UsuarioViaje pertenencia = obtenerPertenenciaActiva(viajeId);

        pertenencia.setPresupuestoIndividual(presupuesto);
        usuarioViajeRepository.save(pertenencia);

        return toParticipanteDTO(pertenencia);
    }

    private Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario autenticado no encontrado"));
    }

    private Viaje obtenerViaje(UUID viajeId) {
        return viajeRepository.findById(viajeId)
                .orElseThrow(() -> new ViajeNoEncontradoException("Viaje no encontrado"));
    }

    private UsuarioViaje obtenerPertenencia(UUID viajeId) {
        Usuario usuario = obtenerUsuarioAutenticado();
        return usuarioViajeRepository
                .findByUsuarioIdAndViajeId(usuario.getId(), viajeId)
                .orElseThrow(() -> new AccesoDenegadoException(
                        "No tiene acceso a este viaje"));
    }

    private UsuarioViaje obtenerPertenenciaActiva(UUID viajeId) {
        Usuario usuario = obtenerUsuarioAutenticado();
        return usuarioViajeRepository
                .findByUsuarioIdAndViajeId(usuario.getId(), viajeId)
                .filter(pertenencia ->
                        pertenencia.getEstadoInvitacion() == EstadoInvitacion.ACEPTADA
                                && pertenencia.isActivo())
                .orElseThrow(() -> new AccesoDenegadoException(
                        "No tiene acceso a este viaje"));
    }

    private UsuarioViaje verificarOrganizador(UUID viajeId) {
        UsuarioViaje pertenencia = obtenerPertenencia(viajeId);
        if (!ROL_PROPIETARIO.equals(pertenencia.getRolEnViaje())) {
            throw new AccesoDenegadoException(
                    "Solo el organizador del viaje puede realizar esta accion");
        }
        return pertenencia;
    }

    private void crearPertenencia(Usuario usuario, Viaje viaje,
                                  EstadoInvitacion estado, String rol) {
        UsuarioViaje pertenencia = new UsuarioViaje();
        pertenencia.setUsuario(usuario);
        pertenencia.setViaje(viaje);
        pertenencia.setEstadoInvitacion(estado);
        pertenencia.setRolEnViaje(rol);
        usuarioViajeRepository.save(pertenencia);
    }

    private void purgarGastosYDeudas(UUID viajeId) {
        // Borrado en cascada de gastos: por cada gasto del viaje se purgan
        // primero sus DeudaGasto asociados y despues se elimina el Gasto.
        // Asi se evitan violaciones de clave foranea y registros huerfanos.
        List<Gasto> gastos = gastoRepository.findByViajeIdOrderByFechaDesc(viajeId);
        for (Gasto gasto : gastos) {
            deudaGastoRepository.deleteByGastoId(gasto.getId());
            gastoRepository.delete(gasto);
        }
    }

    private void purgarDocumentosDelViaje(UUID viajeId) {
        // Borrado inteligente: primero se purgan fisicamente de Cloudflare R2
        // todos los documentos asociados al viaje y luego se eliminan las filas.
        List<Documento> documentos = documentoRepository.findByActividadViajeId(viajeId);
        for (Documento documento : documentos) {
            storageService.eliminarDocumento(documento.getUrlArchivo());
        }
        documentoRepository.deleteByViajeId(viajeId);
    }

    private void purgarFotosYAlbumes(UUID viajeId) {
        // Galeria multimedia: primero se purgan fisicamente las fotos de
        // Cloudflare R2 y luego se eliminan las filas (fotos -> albumes).
        List<Foto> fotos = fotoRepository.findByViajeId(viajeId);
        for (Foto foto : fotos) {
            storageService.eliminarDocumento(foto.getUrl());
        }
        fotoRepository.deleteByViajeId(viajeId);
        albumRepository.deleteByViajeId(viajeId);
    }

    private List<ParticipanteResponseDTO> obtenerParticipantesDTO(Viaje viaje) {
        return usuarioViajeRepository.findByViajeId(viaje.getId()).stream()
                .filter(pertenencia ->
                        pertenencia.getEstadoInvitacion() == EstadoInvitacion.ACEPTADA)
                .map(this::toParticipanteDTO)
                .toList();
    }

    private ParticipanteResponseDTO toParticipanteDTO(UsuarioViaje pertenencia) {
        return new ParticipanteResponseDTO(
                pertenencia.getUsuario().getId(),
                pertenencia.getUsuario().getNombre(),
                pertenencia.getUsuario().getEmail(),
                pertenencia.getRolEnViaje(),
                pertenencia.getUsuario().getAvatarUrl(),
                pertenencia.getPresupuestoIndividual());
    }

    private ViajeResponseDTO toResponseDTO(UsuarioViaje pertenencia) {
        return toResponseDTO(pertenencia.getViaje(), pertenencia.getRolEnViaje());
    }

    private ViajeResponseDTO toResponseDTO(Viaje viaje, String rolEnViaje) {
        return new ViajeResponseDTO(
                viaje.getId(),
                viaje.getTitulo(),
                viaje.getFechaInicio(),
                viaje.getFechaFin(),
                viaje.getPresupuestoBase(),
                viaje.getPresupuestoGlobal(),
                rolEnViaje,
                viaje.getFotoPortada(),
                obtenerParticipantesDTO(viaje));
    }
}
