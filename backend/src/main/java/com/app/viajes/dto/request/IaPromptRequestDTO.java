package com.app.viajes.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para solicitar la generacion de un itinerario con IA.
 */
public record IaPromptRequestDTO(
        @NotBlank(message = "El destino es obligatorio")
        @Size(max = 255, message = "El destino no puede superar 255 caracteres")
        String destino,

        @NotNull(message = "El numero de dias es obligatorio")
        @Min(value = 1, message = "El numero de dias debe ser al menos 1")
        Integer dias,

        @NotBlank(message = "La tematica es obligatoria")
        @Size(max = 500, message = "La tematica no puede superar 500 caracteres")
        String tematica
) {
}
