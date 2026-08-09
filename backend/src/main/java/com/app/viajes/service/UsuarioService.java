package com.app.viajes.service;

import com.app.viajes.dto.UsuarioResponseDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * Servicio de gestion del perfil del usuario autenticado.
 */
public interface UsuarioService {

    /**
     * Actualiza el avatar del usuario autenticado.
     * Si ya existia un avatar previo, borra la imagen antigua de Cloudflare R2
     * antes de guardar la nueva (evita archivos huerfanos en el bucket).
     *
     * @param archivo imagen nueva (multipart, max 5MB configurado)
     * @return datos publicos del usuario con la {@code avatarUrl} actualizada
     */
    UsuarioResponseDTO actualizarAvatar(MultipartFile archivo);
}