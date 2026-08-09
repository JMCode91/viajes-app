package com.app.viajes.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de respuesta con los datos de una foto de la galeria multimedia.
 * Incluye la informacion basica del autor (id y nombre) para pintar su
 * avatar en el frontend, ademas del album al que pertenece (opcional).
 */
public record FotoResponseDTO(
        UUID id,
        UUID viajeId,
        UUID albumId,
        String url,
        LocalDateTime fechaSubida,
        UUID usuarioId,
        String usuarioNombre
) {
}