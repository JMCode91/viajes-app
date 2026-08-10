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

        @Size(max = 255, message = "El googlePlaceId no puede superar los 255 caracteres")
        String googlePlaceId,

        Double rating,

        @Size(max = 500, message = "Los horarios no pueden superar los 500 caracteres")
        String horarios,

        @Size(max = 500, message = "La urlGoogleMaps no puede superar los 500 caracteres")
        String urlGoogleMaps,

        @Size(max = 2000, message = "La descripcionGoogle no puede superar los 2000 caracteres")
        String descripcionGoogle,

        @NotNull(message = "El tipo de actividad es obligatorio")
        TipoActividad tipoActividad,

        @NotNull(message = "La fecha y hora de inicio es obligatoria")
        LocalDateTime fechaHoraInicio,

        @NotNull(message = "La fecha y hora de fin es obligatoria")
        LocalDateTime fechaHoraFin,

        Double latitud,

        Double longitud,

        @Size(max = 255, message = "La direccion no puede superar los 255 caracteres")
        String direccion,

        String notas
) {
}