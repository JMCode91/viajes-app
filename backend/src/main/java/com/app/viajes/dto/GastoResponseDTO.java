package com.app.viajes.dto;

import com.app.viajes.model.enums.TipoDivision;
import com.app.viajes.model.enums.TipoGasto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de respuesta de un gasto de un viaje. Incluye el importe, la fecha,
 * el tipo de gasto, la division, el pagador y las deudas derivadas del
 * gasto compartido. {@code tipoDivision} sera {@code null} para los gastos
 * personales y {@code deudas} estara vacia cuando no haya reparto.
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
        String pagadorNombre,
        List<DetalleDeudaDTO> deudas
) {
}