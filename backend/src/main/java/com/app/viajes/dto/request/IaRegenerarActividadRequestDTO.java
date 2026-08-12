package com.app.viajes.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para regenerar (sustituir) una unica actividad de un
 * itinerario generado por IA. Contiene el contexto del viaje (destino y
 * tematica), la franja horaria de la actividad a sustituir y la actividad
 * actual que el viajero quiere cambiar.
 */
public record IaRegenerarActividadRequestDTO(
        @NotBlank(message = "El destino es obligatorio")
        @Size(max = 255, message = "El destino no puede superar 255 caracteres")
        String destino,

        @NotBlank(message = "La tematica es obligatoria")
        @Size(max = 500, message = "La tematica no puede superar 500 caracteres")
        String tematica,

        @NotBlank(message = "La hora es obligatoria")
        @Size(max = 5, message = "La hora no puede superar 5 caracteres")
        String hora,

        @NotBlank(message = "La actividad actual es obligatoria")
        @Size(max = 255, message = "La actividad actual no puede superar 255 caracteres")
        String actividadActual
) {
}