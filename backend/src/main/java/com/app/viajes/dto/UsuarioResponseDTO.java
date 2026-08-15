package com.app.viajes.dto;

import java.util.UUID;

/**
 * DTO de respuesta con los datos publicos del usuario autenticado,
 * incluida sus preferencias de notificaciones.
 */
public record UsuarioResponseDTO(
        UUID id,
        String email,
        String nombre,
        String avatarUrl,
        String fondoPantallaUrl,
        boolean notificacionesGastos,
        boolean notificacionesPlanes
) {
}