package com.app.viajes.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO de entrada para solicitar la generacion de un itinerario con IA.
 */
public record IaPromptRequestDTO(
        @NotBlank(message = "El destino es obligatorio")
        @Size(max = 255, message = "El destino no puede superar 255 caracteres")
        String destino,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate fechaInicio,

        @NotNull(message = "La fecha de fin es obligatoria")
        LocalDate fechaFin,

        @NotBlank(message = "La tematica es obligatoria")
        @Size(max = 500, message = "La tematica no puede superar 500 caracteres")
        String tematica,

        @Size(max = 1000, message = "Los detalles adicionales no pueden superar 1000 caracteres")
        String detallesAdicionales
) {
}
