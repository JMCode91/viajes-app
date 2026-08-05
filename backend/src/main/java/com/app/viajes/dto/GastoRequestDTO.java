package com.app.viajes.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO de entrada para la creacion de un gasto de un viaje.
 */
public record GastoRequestDTO(
        @NotBlank(message = "El concepto es obligatorio")
        @Size(max = 200, message = "El concepto no puede superar los 200 caracteres")
        String concepto,

        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0.0", message = "La cantidad no puede ser negativa")
        BigDecimal cantidad,

        @NotNull(message = "El pagador es obligatorio")
        UUID pagadorId
) {
}