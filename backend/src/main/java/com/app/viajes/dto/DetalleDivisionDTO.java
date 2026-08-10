package com.app.viajes.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Detalle de participacion de un usuario en la division de un gasto
 * compartido. {@code valorAsignado} representa un porcentaje (division
 * PORCENTAJE) o el importe a asumir segun el tipo de division:
 * <ul>
 *   <li>PORCENTAJE: valor porcentual (deben sumar 100).</li>
 *   <li>DEUDA_TERCERO: un unico detalle con el 100%.</li>
 * </ul>
 */
public record DetalleDivisionDTO(
        @NotNull(message = "El usuario del detalle es obligatorio")
        UUID usuarioId,

        @NotNull(message = "El valor asignado es obligatorio")
        @DecimalMin(value = "0.0", message = "El valor asignado no puede ser negativo")
        BigDecimal valorAsignado
) {
}