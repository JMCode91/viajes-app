package com.app.viajes.dto;

import com.app.viajes.model.enums.TipoDivision;
import com.app.viajes.model.enums.TipoGasto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO de entrada para la creacion de un gasto de un viaje.
 * {@code tipoGasto} y {@code tipoDivision} son opcionales: si no se
 * indican, el servicio aplica los valores por defecto (COMPARTIDO /
 * PARTES_IGUALES).
 */
public record GastoRequestDTO(
        @NotBlank(message = "El concepto es obligatorio")
        @Size(max = 200, message = "El concepto no puede superar los 200 caracteres")
        String concepto,

        @NotNull(message = "El importe es obligatorio")
        @DecimalMin(value = "0.0", message = "El importe no puede ser negativo")
        BigDecimal importe,

        @NotNull(message = "El pagador es obligatorio")
        UUID pagadorId,

        TipoGasto tipoGasto,

        TipoDivision tipoDivision
) {
}