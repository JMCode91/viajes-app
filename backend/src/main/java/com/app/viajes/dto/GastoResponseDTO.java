package com.app.viajes.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO de respuesta de un gasto de un viaje.
 */
public record GastoResponseDTO(
        UUID id,
        UUID viajeId,
        String concepto,
        BigDecimal cantidad,
        UUID pagadorId
) {
}