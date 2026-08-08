package com.app.viajes.dto;

import java.util.UUID;

/**
 * DTO de respuesta con los datos de un documento de una actividad.
 */
public record DocumentoDTO(
        UUID id,
        String nombreOriginal,
        String urlArchivo
) {
}