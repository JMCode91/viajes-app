package com.app.viajes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO de una parada (ciudad/destino) de un viaje multidestino. Viaja tanto
 * en el request de creacion como en la respuesta del viaje, reflejando la
 * entidad {@code ParadaViaje}.
 *
 * <p>El campo {@code id} solo se rellena en las respuestas; al crear un viaje
 * la base de datos lo asigna tras la persistencia.
 */
public record ParadaViajeDTO(
        Long id,

        @NotBlank(message = "El nombre de la ciudad es obligatorio")
        @Size(max = 200, message = "El nombre de la ciudad no puede superar los 200 caracteres")
        String nombreCiudad,

        Double latitud,

        Double longitud,

        @NotNull(message = "La fecha de llegada es obligatoria")
        LocalDate fechaLlegada,

        @NotNull(message = "La fecha de salida es obligatoria")
        LocalDate fechaSalida,

        @NotNull(message = "El orden de la parada es obligatorio")
        Integer orden
) {
}
