package com.app.viajes.service.impl;

import com.app.viajes.dto.FotoResponseDTO;
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
import com.app.viajes.service.FotoService;
import com.app.viajes.service.StorageService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Implementacion del servicio de fotos de la galeria multimedia.
 * Reutiliza {@link StorageService} para subir/borrar los archivos de
 * Cloudflare R2 y asigna al usuario autenticado como autor de cada foto.
 */
@Service
public class FotoServiceImpl implements FotoService {

    private final FotoRepository fotoRepository;
    private final AlbumRepository albumRepository;
    private final ViajeRepository viajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioViajeRepository usuarioViajeRepository;
    private final StorageService storageService;

    public FotoServiceImpl(FotoRepository fotoRepository,
                           AlbumRepository albumRepository,
                           ViajeRepository viajeRepository,
                           UsuarioRepository usuarioRepository,
                           UsuarioViajeRepository usuarioViajeRepository,
                           StorageService storageService) {
        this.fotoRepository = fotoRepository;
        this.albumRepository = albumRepository;
        this.viajeRepository = viajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioViajeRepository = usuarioViajeRepository;
        this.storageService = storageService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FotoResponseDTO> obtenerFotos(UUID viajeId, UUID albumId) {
        verificarPertenencia(viajeId);

        if (albumId != null) {
            verificarAlbumDelViaje(viajeId, albumId);
            return fotoRepository.findByViajeIdAndAlbumIdOrderByFechaSubidaDesc(viajeId, albumId)
                    .stream()
                    .map(this::toResponseDTO)
                    .toList();
        }

        return fotoRepository.findByViajeIdOrderByFechaSubidaDesc(viajeId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public FotoResponseDTO subirFoto(UUID viajeId, UUID albumId, MultipartFile archivo) {
        verificarPertenencia(viajeId);

        Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new ViajeNoEncontradoException("Viaje no encontrado"));

        Album album = null;
        if (albumId != null) {
            album = verificarAlbumDelViaje(viajeId, albumId);
        }

        Usuario autor = obtenerUsuarioAutenticado();

        String url = storageService.subirDocumento(archivo);

        Foto foto = new Foto();
        foto.setViaje(viaje);
        foto.setAlbum(album);
        foto.setAutor(autor);
        foto.setUrl(url);
        fotoRepository.save(foto);

        return toResponseDTO(foto);
    }

    @Override
    @Transactional
    public void eliminarFoto(UUID viajeId, UUID fotoId) {
        verificarPertenencia(viajeId);

        Foto foto = fotoRepository.findById(fotoId)
                .orElseThrow(() -> new AccesoDenegadoException("Foto no encontrada"));

        if (!foto.getViaje().getId().equals(viajeId)) {
            throw new AccesoDenegadoException("La foto no pertenece a este viaje");
        }

        // Borrado inteligente: primero Cloudflare R2, despues el registro.
        storageService.eliminarDocumento(foto.getUrl());
        fotoRepository.delete(foto);
    }

    /**
     * Comprueba que existe el album y que pertenece al viaje indicado.
     */
    private Album verificarAlbumDelViaje(UUID viajeId, UUID albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AccesoDenegadoException("Album no encontrado"));
        if (!album.getViaje().getId().equals(viajeId)) {
            throw new AccesoDenegadoException("El album no pertenece a este viaje");
        }
        return album;
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

    private FotoResponseDTO toResponseDTO(Foto foto) {
        return new FotoResponseDTO(
                foto.getId(),
                foto.getViaje().getId(),
                foto.getAlbum() != null ? foto.getAlbum().getId() : null,
                foto.getUrl(),
                foto.getFechaSubida(),
                foto.getAutor().getId(),
                foto.getAutor().getNombre());
    }
}