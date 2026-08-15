package com.app.viajes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO de entrada para la creacion de un viaje. Incluye la lista opcional de
 * paradas (ciudades/destinos) que define la ruta multidestino; si no viaja
 * en el request, el viaje se crea sin paradas.
 */
public record ViajeRequestDTO(
        @NotBlank(message = "El titulo es obligatorio")
        @Size(max = 200, message = "El titulo no puede superar los 200 caracteres")
        String titulo,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate fechaInicio,

        @NotNull(message = "La fecha de fin es obligatoria")
        LocalDate fechaFin,

        @NotNull(message = "El presupuesto base es obligatorio")
        @DecimalMin(value = "0.0", message = "El presupuesto no puede ser negativo")
        BigDecimal presupuestoBase,

        List<@Valid ParadaViajeDTO> paradas
) {
}