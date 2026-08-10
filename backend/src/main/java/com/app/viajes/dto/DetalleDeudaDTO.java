package com.app.viajes.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Detalle de deuda de un participante (deudor) dentro de un gasto
 * compartido. Representa la cantidad exacta que el deudor le debe al
 * pagador del gasto. El acreedor es siempre el pagador y no se incluye
 * en este DTO porque {@code GastoResponseDTO} ya lo expone.
 */
public record DetalleDeudaDTO(
        UUID deudorId,
        String deudorNombre,
        BigDecimal importe
) {
}