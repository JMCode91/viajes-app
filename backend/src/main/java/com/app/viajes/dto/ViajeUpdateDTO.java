package com.app.viajes.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de entrada para actualizar los datos basicos de un viaje
 * (Panel de Ajustes del Viaje). Adicionalmente permite ajustar el
 * presupuesto global del viaje (opcional, no soporta valores negativos).
 */
public record ViajeUpdateDTO(
        @NotBlank(message = "El titulo es obligatorio")
        @Size(max = 200, message = "El titulo no puede superar los 200 caracteres")
        String titulo,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate fechaInicio,

        @NotNull(message = "La fecha de fin es obligatoria")
        LocalDate fechaFin,

        @DecimalMin(value = "0.0", message = "El presupuesto global no puede ser negativo")
        BigDecimal presupuestoGlobal
) {
}