package com.app.viajes.dto;

import java.util.UUID;

/**
 * DTO de respuesta de un item de checklist.
 */
public record ItemChecklistResponseDTO(
        UUID id,
        UUID viajeId,
        String nombre,
        boolean completado
) {
}