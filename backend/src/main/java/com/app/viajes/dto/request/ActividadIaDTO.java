package com.app.viajes.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO que representa una actividad individual de un dia del itinerario
 * generado por IA. Contiene la hora estimada, el titulo, el lugar exacto
 * (clave para la geolocalizacion en el mapa del frontend), las coordenadas
 * GPS (latitud/longitud) y una descripcion breve de la actividad.
 */
public record ActividadIaDTO(
        @NotBlank(message = "La hora de la actividad es obligatoria")
        @Size(max = 5, message = "La hora no puede superar 5 caracteres")
        String hora,

        @NotBlank(message = "El titulo de la actividad es obligatorio")
        @Size(max = 200, message = "El titulo de la actividad no puede superar 200 caracteres")
        String titulo,

        @NotBlank(message = "El lugar de la actividad es obligatorio")
        @Size(max = 255, message = "El lugar no puede superar 255 caracteres")
        String lugar,

        Double latitud,

        Double longitud,

        @Size(max = 2000, message = "La descripcion no puede superar 2000 caracteres")
        String descripcion
) {
}
