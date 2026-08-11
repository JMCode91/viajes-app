package com.app.viajes.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO que representa un dia del itinerario generado por IA.
 * Contiene el numero de dia, un titulo descriptivo y la lista
 * de actividades propuestas para esa jornada.
 */
public record DiaIaDTO(
        @NotNull(message = "El numero de dia es obligatorio")
        @Min(value = 1, message = "El numero de dia debe ser al menos 1")
        Integer dia,

        @NotBlank(message = "El titulo del dia es obligatorio")
        @Size(max = 200, message = "El titulo del dia no puede superar 200 caracteres")
        String titulo,

        @NotNull(message = "Las actividades del dia son obligatorias")
        @NotEmpty(message = "El dia debe tener al menos una actividad")
        @Valid
        List<ActividadIaDTO> actividades
) {
}
