package com.app.viajes.dto;

import com.app.viajes.model.enums.TipoDivision;
import com.app.viajes.model.enums.TipoGasto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de respuesta de un gasto de un viaje. Incluye el importe, la fecha,
 * el tipo de gasto y la division. {@code tipoDivision} sera {@code null}
 * para los gastos personales.
 */
public record GastoResponseDTO(
        UUID id,
        UUID viajeId,
        String concepto,
        BigDecimal importe,
        LocalDateTime fecha,
        TipoGasto tipoGasto,
        TipoDivision tipoDivision,
        UUID pagadorId,
        String pagadorNombre
) {
}