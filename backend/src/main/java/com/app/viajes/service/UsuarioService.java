package com.app.viajes.service;

import com.app.viajes.dto.UsuarioResponseDTO;
import com.app.viajes.dto.request.CambiarPasswordRequest;
import com.app.viajes.dto.request.PreferenciasNotificacionRequest;
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

    /**
     * Devuelve los datos del usuario autenticado obtenidos del contexto de seguridad.
     *
     * @return datos publicos del usuario autenticado
     */
    UsuarioResponseDTO obtenerPerfil();

    /**
     * Cambia la contrasena del usuario autenticado.
     * Verifica que {@code passwordActual} coincida con la contrasena almacenada
     * (BCrypt) antes de codificar y persistir la {@code nuevaPassword}.
     * Si la contrasena actual no es correcta, lanza {@link com.app.viajes.exception.DatosInvalidosException}.
     *
     * @param request contrasena actual y nueva contrasena
     */
    void cambiarPassword(CambiarPasswordRequest request);

    /**
     * Actualiza exclusivamente las preferencias de notificaciones del usuario
     * autenticado (gastos y planes).
     *
     * @param request preferencias de notificaciones
     * @return datos publicos del usuario con las preferencias actualizadas
     */
    UsuarioResponseDTO actualizarPreferencias(PreferenciasNotificacionRequest request);
}