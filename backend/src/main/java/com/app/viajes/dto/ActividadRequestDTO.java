package com.app.viajes.dto;

import com.app.viajes.model.enums.TipoActividad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO de entrada para la creacion de una actividad de un viaje.
 */
public record ActividadRequestDTO(
        @NotBlank(message = "El titulo es obligatorio")
        @Size(max = 200, message = "El titulo no puede superar los 200 caracteres")
        String titulo,

        String descripcion,

        @NotNull(message = "El tipo de actividad es obligatorio")
        TipoActividad tipoActividad,

        @NotNull(message = "La fecha y hora de inicio es obligatoria")
        LocalDateTime fechaHoraInicio,

        @NotNull(message = "La fecha y hora de fin es obligatoria")
        LocalDateTime fechaHoraFin,

        Double latitud,

        Double longitud
) {
}