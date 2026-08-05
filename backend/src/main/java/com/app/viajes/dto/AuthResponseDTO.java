package com.app.viajes.dto;

/**
 * DTO de respuesta con el token JWT generado y los datos del usuario.
 */
public record AuthResponseDTO(
        String token,
        String email,
        String nombre
) {
}
