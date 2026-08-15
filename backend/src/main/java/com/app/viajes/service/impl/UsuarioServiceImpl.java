package com.app.viajes.service.impl;

import com.app.viajes.dto.UsuarioResponseDTO;
import com.app.viajes.dto.request.CambiarPasswordRequest;
import com.app.viajes.dto.request.PreferenciasNotificacionRequest;
import com.app.viajes.exception.DatosInvalidosException;
import com.app.viajes.model.Usuario;
import com.app.viajes.repository.UsuarioRepository;
import com.app.viajes.security.SecurityUtils;
import com.app.viajes.service.StorageService;
import com.app.viajes.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementacion del servicio de perfil de usuario.
 * Reutiliza {@link StorageService} para subir el avatar a Cloudflare R2,
 * aplica borrado inteligente del avatar anterior antes de persistir el nuevo,
 * gestiona el cambio de contrasena (BCrypt) y las preferencias de notificaciones.
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final StorageService storageService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              StorageService storageService,
                              PasswordEncoder passwordEncoder,
                              SecurityUtils securityUtils) {
        this.usuarioRepository = usuarioRepository;
        this.storageService = storageService;
        this.passwordEncoder = passwordEncoder;
        this.securityUtils = securityUtils;
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizarAvatar(MultipartFile archivo) {
        Usuario usuario = securityUtils.getUsuarioAutenticado();

        // Borrado inteligente: purga el avatar anterior de R2 para no dejar
        // archivos huerfanos en el bucket (no-op si aun no habia avatar).
        borrarAvatarAnterior(usuario);

        String nuevaUrl = storageService.subirImagen(archivo);
        usuario.setAvatarUrl(nuevaUrl);
        usuarioRepository.save(usuario);

        return toResponseDTO(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizarFondoPantalla(MultipartFile archivo) {
        Usuario usuario = securityUtils.getUsuarioAutenticado();

        // Borrado inteligente: purga el fondo anterior de R2 para no dejar
        // archivos huerfanos en el bucket (no-op si aun no habia fondo).
        borrarFondoAnterior(usuario);

        String nuevaUrl = storageService.subirImagen(archivo);
        usuario.setFondoPantallaUrl(nuevaUrl);
        usuarioRepository.save(usuario);

        return toResponseDTO(usuario);
    }

    @Override
    public UsuarioResponseDTO obtenerPerfil() {
        return toResponseDTO(securityUtils.getUsuarioAutenticado());
    }

    @Override
    @Transactional
    public void cambiarPassword(CambiarPasswordRequest request) {
        Usuario usuario = securityUtils.getUsuarioAutenticado();

        validarPasswordActual(usuario, request.passwordActual());

        usuario.setPassword(passwordEncoder.encode(request.nuevaPassword()));
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizarPreferencias(PreferenciasNotificacionRequest request) {
        Usuario usuario = securityUtils.getUsuarioAutenticado();
        usuario.setNotificacionesGastos(request.notificacionesGastos());
        usuario.setNotificacionesPlanes(request.notificacionesPlanes());
        usuarioRepository.save(usuario);
        return toResponseDTO(usuario);
    }

    /**
     * Purga el avatar anterior de Cloudflare R2 (no-op si aun no habia avatar).
     */
    private void borrarAvatarAnterior(Usuario usuario) {
        if (StringUtils.hasText(usuario.getAvatarUrl())) {
            storageService.eliminarDocumento(usuario.getAvatarUrl());
        }
    }

    /**
     * Purga el fondo de pantalla anterior de Cloudflare R2 (no-op si aun no habia fondo).
     */
    private void borrarFondoAnterior(Usuario usuario) {
        if (StringUtils.hasText(usuario.getFondoPantallaUrl())) {
            storageService.eliminarDocumento(usuario.getFondoPantallaUrl());
        }
    }

    /**
     * Verifica que la contrasena actual coincide con la almacenada (BCrypt).
     */
    private void validarPasswordActual(Usuario usuario, String passwordActual) {
        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new DatosInvalidosException("La contrasena actual no es correcta");
        }
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getAvatarUrl(),
                usuario.getFondoPantallaUrl(),
                usuario.isNotificacionesGastos(),
                usuario.isNotificacionesPlanes());
    }
}