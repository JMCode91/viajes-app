package com.app.viajes.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO de entrada para actualizar el presupuesto individual de gastos
 * de un participante dentro de un viaje.
 */
public record ActualizarPresupuestoIndividualDTO(
        @NotNull(message = "El presupuesto es obligatorio")
        @DecimalMin(value = "0.0", message = "El presupuesto no puede ser negativo")
        BigDecimal presupuesto
) {
}