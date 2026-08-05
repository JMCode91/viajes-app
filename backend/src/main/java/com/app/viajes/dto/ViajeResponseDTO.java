package com.app.viajes.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO de respuesta con los datos de un viaje y el rol del usuario
 * autenticado dentro del mismo.
 */
public record ViajeResponseDTO(
        UUID id,
        String titulo,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        BigDecimal presupuestoBase,
        String rolEnViaje
) {
}