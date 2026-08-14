package com.app.viajes.service.impl;

import com.app.viajes.dto.AlbumRequestDTO;
import com.app.viajes.dto.AlbumResponseDTO;
import com.app.viajes.exception.AccesoDenegadoException;
import com.app.viajes.exception.UsuarioNoEncontradoException;
import com.app.viajes.exception.ViajeNoEncontradoException;
import com.app.viajes.model.Album;
import com.app.viajes.model.Foto;
import com.app.viajes.model.Usuario;
import com.app.viajes.model.Viaje;
import com.app.viajes.repository.AlbumRepository;
import com.app.viajes.repository.FotoRepository;
import com.app.viajes.repository.UsuarioRepository;
import com.app.viajes.repository.UsuarioViajeRepository;
import com.app.viajes.repository.ViajeRepository;
import com.app.viajes.service.AlbumService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implementacion del servicio de albumes. Valida la pertenencia del
 * usuario autenticado al viaje en cada operacion.
 */
@Service
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final FotoRepository fotoRepository;
    private final ViajeRepository viajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioViajeRepository usuarioViajeRepository;

    public AlbumServiceImpl(AlbumRepository albumRepository,
                            FotoRepository fotoRepository,
                            ViajeRepository viajeRepository,
                            UsuarioRepository usuarioRepository,
                            UsuarioViajeRepository usuarioViajeRepository) {
        this.albumRepository = albumRepository;
        this.fotoRepository = fotoRepository;
        this.viajeRepository = viajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioViajeRepository = usuarioViajeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlbumResponseDTO> obtenerAlbumes(UUID viajeId) {
        verificarPertenencia(viajeId);
        return albumRepository.findByViajeIdOrderByFechaCreacionDesc(viajeId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public AlbumResponseDTO crearAlbum(UUID viajeId, AlbumRequestDTO dto) {
        verificarPertenencia(viajeId);

        Viaje viaje = obtenerViajeValidado(viajeId);

        Album album = new Album();
        album.setViaje(viaje);
        album.setNombre(dto.nombre().trim());
        albumRepository.save(album);

        return toResponseDTO(album);
    }

    @Override
    @Transactional
    public void eliminarAlbum(UUID viajeId, UUID albumId) {
        verificarPertenencia(viajeId);

        Album album = obtenerAlbumValidado(viajeId, albumId);

        // Las fotos no se borran al eliminar el album: se desasocian (album
        // nullable) y pasan a la galeria "sin album".
        desasociarFotosDelAlbum(albumId);

        albumRepository.delete(album);
    }

    /**
     * Busca el viaje por su id o lanza la excepcion de dominio si no existe.
     */
    private Viaje obtenerViajeValidado(UUID viajeId) {
        return viajeRepository.findById(viajeId)
                .orElseThrow(() -> new ViajeNoEncontradoException("Viaje no encontrado"));
    }

    /**
     * Busca un album y verifica que pertenece al viaje indicado (anti-IDOR).
     */
    private Album obtenerAlbumValidado(UUID viajeId, UUID albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AccesoDenegadoException("Album no encontrado"));

        if (!album.getViaje().getId().equals(viajeId)) {
            throw new AccesoDenegadoException("El album no pertenece a este viaje");
        }

        return album;
    }

    /**
     * Desasocia las fotos de un album (album nullable) para que pasen a la
     * galeria "sin album" en lugar de borrarse junto al album.
     */
    private void desasociarFotosDelAlbum(UUID albumId) {
        List<Foto> fotos = fotoRepository.findByAlbumId(albumId);
        fotos.forEach(foto -> foto.setAlbum(null));
        if (!fotos.isEmpty()) {
            fotoRepository.saveAll(fotos);
        }
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

    private AlbumResponseDTO toResponseDTO(Album album) {
        return new AlbumResponseDTO(
                album.getId(),
                album.getViaje().getId(),
                album.getNombre(),
                album.getFechaCreacion());
    }
}