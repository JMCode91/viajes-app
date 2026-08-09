package com.app.viajes.dto;

import java.util.UUID;

/**
 * DTO de respuesta con los datos de un participante de un viaje
 * (usuarios vinculados al viaje y su rol).
 */
public record ParticipanteResponseDTO(
        UUID id,
        String nombre,
        String email,
        String rolEnViaje,
        String avatarUrl
) {
}