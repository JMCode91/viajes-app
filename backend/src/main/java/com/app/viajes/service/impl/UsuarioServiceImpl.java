package com.app.viajes.service.impl;

import com.app.viajes.dto.UsuarioResponseDTO;
import com.app.viajes.exception.UsuarioNoEncontradoException;
import com.app.viajes.model.Usuario;
import com.app.viajes.repository.UsuarioRepository;
import com.app.viajes.service.StorageService;
import com.app.viajes.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementacion del servicio de perfil de usuario.
 * Reutiliza {@link StorageService} para subir el avatar a Cloudflare R2 y
 * aplica borrado inteligente del avatar anterior antes de persistir el nuevo.
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final StorageService storageService;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              StorageService storageService) {
        this.usuarioRepository = usuarioRepository;
        this.storageService = storageService;
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizarAvatar(MultipartFile archivo) {
        Usuario usuario = obtenerUsuarioAutenticado();

        // Borrado inteligente: purga el avatar anterior de R2 para no dejar
        // archivos huerfanos en el bucket (no-op si aun no habia avatar).
        if (StringUtils.hasText(usuario.getAvatarUrl())) {
            storageService.eliminarDocumento(usuario.getAvatarUrl());
        }

        String nuevaUrl = storageService.subirDocumento(archivo);
        usuario.setAvatarUrl(nuevaUrl);
        usuarioRepository.save(usuario);

        return toResponseDTO(usuario);
    }

    private Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario autenticado no encontrado"));
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getAvatarUrl());
    }
}