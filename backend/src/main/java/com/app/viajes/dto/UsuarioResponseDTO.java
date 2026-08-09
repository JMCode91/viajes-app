package com.app.viajes.dto;

import java.util.UUID;

/**
 * DTO de respuesta con los datos publicos del usuario autenticado.
 */
public record UsuarioResponseDTO(
        UUID id,
        String email,
        String nombre,
        String avatarUrl
) {
}