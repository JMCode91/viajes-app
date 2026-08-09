package com.app.viajes.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de respuesta con los datos de un album de fotos de un viaje.
 */
public record AlbumResponseDTO(
        UUID id,
        UUID viajeId,
        String nombre,
        LocalDateTime fechaCreacion
) {
}